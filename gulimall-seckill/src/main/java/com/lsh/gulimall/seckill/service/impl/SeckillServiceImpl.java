package com.lsh.gulimall.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.google.common.reflect.TypeToken;
import com.lsh.gulimall.common.to.mq.SeckillOrderTo;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.common.vo.MemberRespVo;
import com.lsh.gulimall.seckill.feign.CouponFeignClient;
import com.lsh.gulimall.seckill.feign.ProductFeignClient;
import com.lsh.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.lsh.gulimall.seckill.service.SeckillService;
import com.lsh.gulimall.seckill.to.SeckillSkuRedisTo;
import com.lsh.gulimall.seckill.vo.SeckillSessionWithSkus;
import com.lsh.gulimall.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeckillServiceImpl implements SeckillService {

	@Autowired
	CouponFeignClient couponFeignClient;

	/*
	 * RedisTemplate使用的是 JdkSerializationRedisSerializer
	 * StringRedisTemplate使用的是 StringRedisSerializer
	 * */
	@Autowired
	StringRedisTemplate redisTemplatel;

	@Autowired
	ProductFeignClient productFeignClient;

	@Autowired
	RedissonClient redissonClient;

	@Autowired
	RabbitTemplate rabbitTemplate;


	private final String SESSION_CACHE_PREFIX = "seckill:session:";
	private final String SKUKILL_CACHE_PREFIX = "seckill:skus:";
	private final String SKU_STOCK_SEMAPHORE = "seckill:stock:"; // 加上商品随机码


	@Override
	public void uploadSeckillSkuLatest3Days() {
		// 1、扫描需要秒杀的服务 远程调用coupon服务
		R r = couponFeignClient.getLatest3DaySession();
		if (r.getCode() == 0) {
			List<SeckillSessionWithSkus> skuHasStockList = r.getData(new TypeReference<List<SeckillSessionWithSkus>>() {
			});
			// 上架商品 缓存到redis
			if (skuHasStockList != null && !skuHasStockList.isEmpty()) {
				// 1、活动信息
				saveSessionInfos(skuHasStockList);
				// 2、活动商品信息
				saveSessionSkuInfos(skuHasStockList);
			}

		}
	}


	/**
	 * //TODO
	 *
	 * @param skuHasStockList
	 * @return: void
	 * @Description: redis存放每个活动以及对应的商品skuId
	 * KEY 活动id活动开始时间以及结束时间
	 * <p>
	 * redisTemplatel.opsForList().leftPushAll(key, skuIds) 从左至右存放一个list
	 */
	private void saveSessionInfos(List<SeckillSessionWithSkus> skuHasStockList) {
		if (skuHasStockList != null) {
			skuHasStockList.forEach(session -> {
				// 获取时间戳
				Long startTime = session.getStartTime().getTime();
				Long endTime = session.getEndTime().getTime();
				String key = SESSION_CACHE_PREFIX + session.getId().toString() + "_" + startTime + "_" + endTime;

				// 幂等性 redis是否已经存在活动信息
				if (!redisTemplatel.hasKey(key)) {
					//收集商品skuid
					// s.getPromotionSessionId()+"_"+s.getSkuId() 避免场次中的商品重复
					List<String> skuIds = session.getRelationEntityList().stream().map(s -> s.getPromotionSessionId() + "_" + s.getSkuId().toString()).collect(Collectors.toList());
					// 放入redis
					redisTemplatel.opsForList().leftPushAll(key, skuIds);
				} else {
					log.info("已经存在活动:{}", key);
				}
			});
		}
	}

	private void saveSessionSkuInfos(List<SeckillSessionWithSkus> skuHasStockList) {
		skuHasStockList.forEach(session -> {
			BoundHashOperations<String, Object, Object> boundHashOps = redisTemplatel.boundHashOps(SKUKILL_CACHE_PREFIX);
			if (session.getRelationEntityList() != null) {
				session.getRelationEntityList().forEach(seckillSkuVo -> {
					//				stringRedisTemplatel JSON.toJSONString(redisTo)
					// 幂等性 redis是否存在商品信息
					if (!boundHashOps.hasKey(seckillSkuVo.getPromotionSessionId() + "_" + seckillSkuVo.getSkuId().toString())) {
						SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
						// 1、sku基本信息
						R info = productFeignClient.getSkuInfo(seckillSkuVo.getSkuId());
						if (info.getCode() == 0) {
							SkuInfoVo skuInfo = info.getData("skuInfo", SkuInfoVo.class);
							if (skuInfo != null) {
								redisTo.setSkuInfoVo(skuInfo);
							}
						}
						// 2、sku秒杀信息
						BeanUtils.copyProperties(seckillSkuVo, redisTo);

						// 3、随机码 开始时间 结束时间
						Long startTime = session.getStartTime().getTime();
						Long endTime = session.getEndTime().getTime();
						redisTo.setStartTime(startTime);
						redisTo.setEndTime(endTime);
						// 随机码 防止暴力发请求抢购
						String token = UUID.randomUUID().toString().replace("-", "");
						redisTo.setRandomCode(token);

						// 如果当前场次商品库存信息已存在 不需要存入redis
						// 设置分布式信号量 锁  应对高并发 秒杀库存
						RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
						// 设置(可以秒杀的数量)库存为信号量 限流
						semaphore.trySetPermits(seckillSkuVo.getSeckillCount().intValue());
						// 4、保存 seckillSkuVo.getPromotionSessionId() + "_" + redisTo.getId().toString()
						boundHashOps.put(seckillSkuVo.getPromotionSessionId() + "_" + redisTo.getSkuId().toString(), JSON.toJSONString(redisTo));
						log.info("上架成功:{}", redisTo.getId().toString());
					} else {
						log.info("已经存在商品信息 skuId:{}", seckillSkuVo.getPromotionSessionId() + "_" + seckillSkuVo.getId().toString());
					}
				});
			}
		});
	}


	/**
	 * //TODO
	 *
	 * @param
	 * @return: List<SeckillSkuRedisTo>
	 * @Description: 当前时间可以秒杀的商品
	 */
	@Override
	@SentinelResource(value = "getCurrentSeckillSkusResource", blockHandler = "getCurrentSeckillSkusBlockHandler")
	public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {
		List<SeckillSkuRedisTo> list = new ArrayList<>();
		// 1、确定当前时间的秒杀场次
		// 获取当前时间戳
		long now = System.currentTimeMillis();
		// 匹配key
		// 使用sentinel 保护资源 资源名称 seckillSkus
		try (Entry seckillSkus = SphU.entry("seckillSkus")) {
			Set<String> sessionKeys = redisTemplatel.keys(SESSION_CACHE_PREFIX + "*");
			for (String key : sessionKeys) {
				// key : SESSION_CACHE_PREFIX + session.getId().toString() + startTime + "_" + endTime;
				String[] split = key.replace(SESSION_CACHE_PREFIX, "").split("_");
				long startTime = Long.parseLong(split[1]);
				long endTime = Long.parseLong(split[2]);

				// 符合秒杀场次
				// 2、获取当前场次的商品信息
				if (now > startTime && now < endTime) {
					Long size = redisTemplatel.opsForList().size(key);
					List<String> range = redisTemplatel.opsForList().range(key, 0, size - 1);
					BoundHashOperations<String, String, String> boundHashOps = redisTemplatel.boundHashOps(SKUKILL_CACHE_PREFIX);
					if (range != null) {
						List<String> items = boundHashOps.multiGet(range);
						List<SeckillSkuRedisTo> collect = JSON.parseObject(items.toString(), new TypeToken<List<SeckillSkuRedisTo>>() {
						}.getType());
//					List<SeckillSkuRedisTo> collect = range.stream().map(s -> {
//						SeckillSkuRedisTo seckillSkuRedisTo = JSON.parseObject(String.valueOf(boundHashOps.get(s)), SeckillSkuRedisTo.class);
////						seckillSkuRedisTo.setRandomCode(null);
////						log.warn("秒杀商品 : {}", seckillSkuRedisTo);
//						return seckillSkuRedisTo;
//					}).collect(Collectors.toList());
						list.addAll(collect);
					}
				}
			}
		} catch (BlockException e) {
			log.error("资源限流{}", e.getMessage());
			e.printStackTrace();
		}
		return list;
	}

	// 熔断降级调用的方法
	public List<SeckillSkuRedisTo> getCurrentSeckillSkusBlockHandler(BlockException blockException) {
		blockException.printStackTrace();
		log.warn("注解方式熔断降级 getCurrentSeckillSkusBlockHandler");
		ArrayList<SeckillSkuRedisTo> list = new ArrayList<>();
		return list;
	}


	// 返回当前商品是否参与秒杀
	@Override
	public SeckillSkuRedisTo getSkuSeckillInfo(Long skuId) {
		BoundHashOperations<String, String, String> boundHashOps = redisTemplatel.boundHashOps(SKUKILL_CACHE_PREFIX);
		Set<String> keys = boundHashOps.keys();
		if (keys != null && !keys.isEmpty() && keys.size() > 0) {
			for (String key : keys) {
				// skuId
				Long redisSkuId = Long.parseLong(key.split("_")[1]);
				if (redisSkuId.equals(skuId)) {
					String seckillSku = boundHashOps.get(key);
					SeckillSkuRedisTo seckillSkuRedisTo = JSON.parseObject(seckillSku, SeckillSkuRedisTo.class);
					// 如果参加活动 返回详细数据
					// 处理随机码
					Long startTime = seckillSkuRedisTo.getStartTime();
					Long endTime = seckillSkuRedisTo.getEndTime();
					long now = System.currentTimeMillis();
					if (now >= startTime && now <= endTime) {
					} else {
						seckillSkuRedisTo.setRandomCode("");
					}
					return seckillSkuRedisTo;
				}
			}
		}
		return null;
	}

	// 秒杀
	// TODO 上架秒杀的时候 每一个数据都有过期时间
	// TODO 秒杀后续流程
	// TODO 生成订单应该发往延时队列 处理支付逾期
	// TODO 定时任务 定期清理redis过期活动

	/**
	 * //TODO
	 *
	 * @param killId 秒杀skuId
	 * @param key    随机码
	 * @param num    秒杀数量
	 * @return: String
	 * @Description:
	 */
	@Override
	public String kill(String killId, String key, Integer num) {

		log.warn("秒杀开始{},{},{}", killId, key, num);
		// 计算一下秒杀耗时
		long start = System.currentTimeMillis();

		BoundHashOperations<String, String, String> boundHashOps = redisTemplatel.boundHashOps(SKUKILL_CACHE_PREFIX);
		String s = boundHashOps.get(killId);
		if (StringUtils.isEmpty(s)) {
			return null;
		}
		SeckillSkuRedisTo seckillSkuRedisTo = JSON.parseObject(s, SeckillSkuRedisTo.class);
		// 购物数量
		if (num > seckillSkuRedisTo.getSeckillLimit().intValue() || num <= 0) {
			return null;
		}
		// 校验合法性
		Long startTime = seckillSkuRedisTo.getStartTime();
		Long endTime = seckillSkuRedisTo.getEndTime();
		long now = System.currentTimeMillis();
		// 秒杀时间错误
		if (now < startTime || now > endTime) {
			return null;
		}
		// 随机码匹配
		if (!seckillSkuRedisTo.getRandomCode().equals(key)) {
			return null;
		}
		// 幂等性 验证用户是否已经购买 如果秒杀成功 去redis占位 userid_session_skuid 超时时间为 现在时间与结束时间差
		MemberRespVo memberRespVo = LoginUserInterceptor.threadLocal.get();
		Long ttl = endTime - now;
		String redis_key = memberRespVo.getId() + "_" + killId;
		// 过期时间: 活动结束
		Boolean aBoolean = redisTemplatel.opsForValue().setIfAbsent(redis_key, num.toString(), ttl, TimeUnit.MILLISECONDS);
		if (aBoolean) {
			// 占位成功   减分布式信号量 acquire阻塞的 tryAcquire非阻塞
			try {
				// tryAcquire redis信号量 缓存减库存
				boolean b = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + key).tryAcquire(num, 200, TimeUnit.MILLISECONDS);
				// 拿到信号量 秒杀成功 快速下单 发送mq消息 直接生成一个订单号
				if (b) {
					String orderSn = IdWorker.getTimeId(); //订单号
					SeckillOrderTo seckillOrderTo = new SeckillOrderTo(); // 订单信息
					seckillOrderTo.setOrderSn(orderSn);
					seckillOrderTo.setMemberId(memberRespVo.getId()); // 购买人
					seckillOrderTo.setNum(num); //数量
					seckillOrderTo.setSkuId(seckillSkuRedisTo.getSkuId()); // 商品id
					seckillOrderTo.setPromotionSessionId(seckillSkuRedisTo.getPromotionSessionId()); // 活动场次
					seckillOrderTo.setSeckillPrice(seckillSkuRedisTo.getSeckillPrice()); // 秒杀价格
					// 发送到消息队列
					rabbitTemplate.convertAndSend("order-event-exchange", "order.seckill.order", seckillOrderTo);
					// 计算一下秒杀耗时
					long end = System.currentTimeMillis();
					log.warn("秒杀耗时 {} 毫秒", end - start);
					return orderSn;
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
		return null;
	}
}