package com.lsh.gulimall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.to.SkuHasStockTo;
import com.lsh.gulimall.common.to.mq.OrderTo;
import com.lsh.gulimall.common.to.mq.SeckillOrderTo;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.common.vo.MemberRespVo;
import com.lsh.gulimall.order.constant.OrderConstant;
import com.lsh.gulimall.order.dao.OrderDao;
import com.lsh.gulimall.order.entity.OrderEntity;
import com.lsh.gulimall.order.entity.OrderItemEntity;
import com.lsh.gulimall.order.entity.PaymentInfoEntity;
import com.lsh.gulimall.order.enume.OrderStatusEnum;
import com.lsh.gulimall.order.feign.CartService;
import com.lsh.gulimall.order.feign.MemberService;
import com.lsh.gulimall.order.feign.ProductService;
import com.lsh.gulimall.order.feign.WareService;
import com.lsh.gulimall.order.interceptor.LoginUserInterceptor;
import com.lsh.gulimall.order.service.OrderItemService;
import com.lsh.gulimall.order.service.OrderService;
import com.lsh.gulimall.order.service.PaymentInfoService;
import com.lsh.gulimall.order.to.OrderCreateTo;
import com.lsh.gulimall.order.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * //TODO
 *
 * @Author: codestar
 * @Date: 2021-09-09 01:44:19
 * @Description:
 */
@Slf4j
@Service("OrderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

	private MemberService memberService;
	private CartService cartService;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	private ThreadPoolExecutor threadPoolExecutor;

	@Autowired
	private WareService wareService;

	@Autowired
	StringRedisTemplate redisTemplate;

	private ThreadLocal<OrderSubmitVo> confirmThreadLocal = new ThreadLocal<>();

	@Autowired
	ProductService productService;


	@Autowired
	OrderItemService orderItemService;

	@Autowired
	LoginUserInterceptor loginUserInterceptor;

	@Autowired
	PaymentInfoService paymentInfoService;


	/**
	 * //TODO
	 *
	 * @param memberService
	 * @return: null
	 * @Description: set方法注入 去除警告 Field injection is not recommended
	 */
	@Autowired
	private void setMemberService(MemberService memberService) {
		this.memberService = memberService;
	}

	@Autowired
	private void setCartService(CartService cartService) {
		this.cartService = cartService;
	}

	/**
	 * //TODO
	 *
	 * @param params
	 * @return: PageUtils
	 * @Description:
	 */
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<OrderEntity> page = this.page(
				new Query<OrderEntity>().getPage(params),
				new QueryWrapper<>()
		);


		return new PageUtils(page);
	}

	/**
	 * //TODO
	 *
	 * @param
	 * @return: OrderConfirmVo
	 * @Description: 获取购物车里全部购物项以及收货地址 优惠信息等数据 异步编排
	 * !!(异步编排下  新的线程无法获得 ThreadLocal的数据 空指针异常)
	 * 解决方案 : 在每一个新的线程开始之前 在新线程的 RequestContextHolder 放入header的数据(在RequestInterceptor之前) 这样RequestInterceptor中的RequestContextHolder就可以拿到数据
	 * (ServletRequestAttributes)RequestAttributes 转换 后可以拿到request
	 * RequestContextHolder.setRequestAttributes(requestAttributes); 放入主线程数据
	 * <p>
	 * 必须调用get方法或者join方法 等待异步执行完成
	 * join()方法抛出的是uncheck异常（即未经检查的异常),不会强制开发者抛出 会将异常包装成CompletionException异常 /CancellationException异常，但是本质原因还是代码内存在的真正的异常 没有返回值
	 * get()方法抛出的是经过检查的异常，ExecutionException, InterruptedException 需要用户手动处理（抛出或者 try catch） 可以自定义返回值
	 * <p>
	 * get和join只是阻塞直到所有阶段都完成，它不会触发计算；计算会立即安排
	 * <p>
	 * <p>
	 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	@Override
	public OrderConfirmVo confirmOrder() {
		OrderConfirmVo confirmVo = new OrderConfirmVo();
		MemberRespVo memberRespVo = LoginUserInterceptor.threadLocal.get();
		Long memberId = memberRespVo.getId();
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();


		CompletableFuture<Void> completableFuture1 = CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {
				/*调用之前放入请求头数据*/
				RequestContextHolder.setRequestAttributes(requestAttributes);
				R r = memberService.orderAddressInfo(memberId);
				if (r.getCode() != 0) {
					System.out.println(r.getCode());
				}
				List<MemberAddressVo> address = (List<MemberAddressVo>) r.get("address");
				/*收货地址*/
				confirmVo.setAddress(address);
			}
		}, threadPoolExecutor);


		/*查询购物项后查询库存信息*/
		CompletableFuture<Void> completableFuture2 = CompletableFuture.runAsync(new Runnable() {
			@Override
			public void run() {
				/*调用之前放入请求头数据*/
				RequestContextHolder.setRequestAttributes(requestAttributes);
				List<OrderItemVo> orderItemVos = cartService.orderCartItemList();
				if (orderItemVos != null && orderItemVos.size() > 0) {
					confirmVo.setItems(orderItemVos);
				}
			}
		}, threadPoolExecutor).thenRunAsync(new Runnable() {
			@Override
			public void run() {
				RequestContextHolder.setRequestAttributes(requestAttributes);
				List<OrderItemVo> items = confirmVo.getItems();
				/*收集所有skuId*/
				List<Long> collect = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
				R r = wareService.haStock(collect);
				List<SkuHasStockTo> skuHasStockList = r.getData(new TypeReference<List<SkuHasStockTo>>() {
				});
				System.out.println("r=========>" + r);
				if (skuHasStockList != null && skuHasStockList.size() > 0) {
					/*获取对应的库存map*/
					Map<Long, Boolean> map = skuHasStockList.stream().collect(Collectors.toMap(SkuHasStockTo::getSkuId, SkuHasStockTo::isHasStock));

					/*存入库存信息*/
					confirmVo.setStocks(map);
					log.info("库存查询成功{}", map);
				} else {
					confirmVo.setStocks(new HashMap<>());
				}

			}
		});


		/*等待异步任务执行完成*/
		CompletableFuture<Void> completableFuture = CompletableFuture.allOf(completableFuture1, completableFuture2);
//		completableFuture.join();
		try {
			/*必须调用get方法或者join方法 等待异步执行完成*/
			completableFuture.get();
		} catch (Exception e) {
			e.printStackTrace();
		}


		Integer integration = memberRespVo.getIntegration();
		confirmVo.setIntegration(integration);

//		TODO  为了防止订单重复提交 防重令牌 原子性校验 接口幂等性
		String orderToken = UUID.randomUUID().toString().replace("-", "");

		/*页面*/
		confirmVo.setOrderToken(orderToken);

		/*服务器*/
		redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOEN_PREFIX + memberRespVo.getId(), orderToken, 30, TimeUnit.MINUTES);
		log.info(String.valueOf(confirmVo));
		return confirmVo;
	}

	/**
	 * //TODO
	 *
	 * @param null
	 * @return: null
	 * @Description: 提交订单
	 */
	@Override
	@Transactional
	public SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo) {
		SubmitOrderResponseVo responseVo = new SubmitOrderResponseVo();
		responseVo.setCode(0);
		/*验证令牌*/
		// 获取用户信息
		MemberRespVo memberRespVo = LoginUserInterceptor.threadLocal.get();
		if (memberRespVo.getId() == null) {
			responseVo.setCode(1);
			return responseVo;
		}
		/*原子性 验证删除令牌*/
		String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] " +
				"then " +
				"    return redis.call(\"del\",KEYS[1]) " +
				"else " +
				"    return 0 " + // 0失败
				"end ";
		String orderToken = orderSubmitVo.getOrderToken();
		/*原子验证删除*/
		Long res = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList(OrderConstant.USER_ORDER_TOEN_PREFIX + memberRespVo.getId()), orderToken);

		if (res == null || res == 0L) {
			/*验证失败*/
			log.warn("---tooken令牌不一致");
			responseVo.setCode(1);
			return responseVo;
		}
//		if (orderToken == null || !orderToken.equals(orderSubmitVo.getOrderToken())) {
//			responseVo.setCode(2);
//		}
		/*验证价格*/
		/*创建订单*/
		confirmThreadLocal.set(orderSubmitVo);
		OrderCreateTo order = createOrder();
		/*新计算价格*/
		BigDecimal payAmount = order.getOrder().getPayAmount();
		/*前端价格*/
		BigDecimal payPrice = orderSubmitVo.getPayPrice();
		/*验价成功*/
		if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {
			/*保存订单*/
			saveOrder(order);
			/*锁定库存 有异常回滚*/
			// 订单号 所有订单项 （skuId skuName,num）
			WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
			wareSkuLockVo.setOrderSn(order.getOrder().getOrderSn());
			List<OrderItemVo> collect = order.getItems().stream().map(item -> {
				OrderItemVo orderItemVo = new OrderItemVo();
				orderItemVo.setSkuId(item.getSkuId());
				orderItemVo.setCount(item.getSkuQuantity());
				orderItemVo.setTitle(item.getSkuName());
				return orderItemVo;
			}).collect(Collectors.toList());
			wareSkuLockVo.setLocks(collect);
			// TODO 远程锁定库存
			// 为了保证高并发，库存服务自己回滚，--》发消息给库存服务
			// 库存服务本身也可以使用自动解锁 --》消息队列  延迟队列
			R r = wareService.orderLockStock(wareSkuLockVo);
			if (r.getCode() != 0) {
				/*锁定失败 */
				responseVo.setCode(3);
				log.warn(String.valueOf(r.get("msg")));
				/*抛出异常回滚*/
//				throw new NoStockException(order.getItems().get(0).getSkuId());
			}
			responseVo.setOrder(order.getOrder());

			// 订单创建成功 发送消息到mq
			rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());

		} else {
			responseVo.setCode(2);
			log.warn("---价格不一致!");
		}
		return responseVo;
	}

	/**
	 * //TODO
	 *
	 * @param OrderCreateTo
	 * @return: null
	 * @Description: 保存订单数据
	 */
	private void saveOrder(OrderCreateTo order) {
		OrderEntity orderEntity = order.getOrder();
		orderEntity.setModifyTime(new Date());
		this.save(orderEntity);
		orderItemService.saveBatch(order.getItems());
	}

	/**
	 * //TODO
	 *
	 * @param null
	 * @return: null
	 * @Description: 创建订单
	 */
	private OrderCreateTo createOrder() {

		OrderCreateTo orderCreateTo = new OrderCreateTo();
		/*1.雪花算法生成订单号*/
		String orderSn = IdWorker.getTimeId();
		/*构建订单*/
		OrderEntity OrderEntity = buildOrder(orderSn);

		/*2.构建所有订单项*/
		List<OrderItemEntity> orderEntityList = buildOrderItems(orderSn);

		/*3.计算价格相关*/
		if (OrderEntity != null && orderEntityList != null) {
			computePrice(OrderEntity, orderEntityList);
		}
		orderCreateTo.setOrder(OrderEntity);
		orderCreateTo.setItems(orderEntityList);
		return orderCreateTo;
	}

	/**
	 * //TODO
	 *
	 * @param null
	 * @return: null
	 * @Description: 计算价格相关
	 */
	private void computePrice(OrderEntity entity, List<OrderItemEntity> orderEntityList) {

		//总价
		BigDecimal total = BigDecimal.ZERO;
		//优惠价格
		BigDecimal promotion = new BigDecimal("0.0");
		BigDecimal integration = new BigDecimal("0.0");
		BigDecimal coupon = new BigDecimal("0.0");
		//积分
		Integer integrationTotal = 0;
		Integer growthTotal = 0;

		for (OrderItemEntity orderItemEntity : orderEntityList) {
			total = total.add(orderItemEntity.getRealAmount());
			promotion = promotion.add(orderItemEntity.getPromotionAmount());
			integration = integration.add(orderItemEntity.getIntegrationAmount());
			coupon = coupon.add(orderItemEntity.getCouponAmount());
			integrationTotal += orderItemEntity.getGiftIntegration();
			growthTotal += orderItemEntity.getGiftGrowth();
		}

		entity.setTotalAmount(total);
		entity.setPromotionAmount(promotion);
		entity.setIntegrationAmount(integration);
		entity.setCouponAmount(coupon);
		entity.setIntegration(integrationTotal);
		entity.setGrowth(growthTotal);

		//付款价格=商品价格+运费
		entity.setPayAmount(entity.getFreightAmount().add(total));

		//设置删除状态(0-未删除，1-已删除)
		entity.setDeleteStatus(0);
	}

	private OrderEntity buildOrder(String orderSn) {


		OrderEntity OrderEntity = new OrderEntity();
		OrderEntity.setOrderSn(orderSn);
		/*会员信息*/
		OrderEntity.setMemberId(LoginUserInterceptor.threadLocal.get().getId());
		OrderEntity.setMemberUsername(LoginUserInterceptor.threadLocal.get().getUsername());
		// 获取收获地址
		OrderSubmitVo orderSubmitVo = confirmThreadLocal.get();
		R fare = wareService.getFare(orderSubmitVo.getAddrId());
		if (fare.getCode() != 0) {
			/*获取收获地址失败*/
			log.error("获取收获地址失败");
			return null;
		} else {
			FareVo fareVo = fare.getData(new TypeReference<FareVo>() {
			});
			/*运费信息，收货人信息*/
			OrderEntity.setFreightAmount(fareVo.getFare());
			OrderEntity.setReceiverCity(fareVo.getAddressEntity().getCity());
			OrderEntity.setReceiverDetailAddress(fareVo.getAddressEntity().getDetailAddress());
			OrderEntity.setReceiverName(fareVo.getAddressEntity().getName());
			OrderEntity.setReceiverPhone(fareVo.getAddressEntity().getPhone());
			OrderEntity.setReceiverPostCode(fareVo.getAddressEntity().getPostCode());
			OrderEntity.setReceiverProvince(fareVo.getAddressEntity().getProvince());
			OrderEntity.setReceiverRegion(fareVo.getAddressEntity().getRegion());
			OrderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
			OrderEntity.setAutoConfirmDay(7);
			return OrderEntity;
		}
	}

	/*构建订单项*/
	private List<OrderItemEntity> buildOrderItems(String orderSn) {
		// 最后确定每一个购物项的价格
		List<OrderItemVo> cartItemList = cartService.orderCartItemList();
		if (cartItemList != null && cartItemList.size() > 0) {
			List<OrderItemEntity> collect = cartItemList.stream().map(cartItem -> {
				/*构建订单项*/
				OrderItemEntity orderEntity = buildOrderItem(cartItem);
				orderEntity.setOrderSn(orderSn);
				return orderEntity;
			}).collect(Collectors.toList());
			return collect;
		}
		return null;
	}

	/*构建一个订单项*/
	private OrderItemEntity buildOrderItem(OrderItemVo orderItemVo) {
		OrderItemEntity OrderEntity = new OrderItemEntity();
		/*1.订单信息 订单号*/
		/*2.spu信息*/
		Long skuId = orderItemVo.getSkuId();
		// 通过skuId查找spu信息
		R spuInfo = productService.getSpuInfo(skuId);
		SpuInfoVo spuInfoVo = spuInfo.getData(new TypeReference<SpuInfoVo>() {
		});
		if (spuInfoVo != null) {
			OrderEntity.setSpuId(spuInfoVo.getId());
			OrderEntity.setSpuBrand(spuInfoVo.getBrandId().toString());
			OrderEntity.setSpuName(spuInfoVo.getSpuName());
			OrderEntity.setCategoryId(spuInfoVo.getCatalogId());
		}

		/*3.sku信息*/
		OrderEntity.setSkuId(skuId);
		OrderEntity.setSkuName(orderItemVo.getTitle());
		OrderEntity.setSkuPic(orderItemVo.getImage());
		OrderEntity.setSkuPrice(orderItemVo.getPrice());
		// list转string delim:  分隔符
		String skuAttrs = StringUtils.collectionToDelimitedString(orderItemVo.getSkuAttr(), ";");
		OrderEntity.setSkuAttrsVals(skuAttrs);
		OrderEntity.setSkuQuantity(orderItemVo.getCount());

		/*4.优惠信息*/
		/*5.积分信息*/
		OrderEntity.setGiftGrowth(orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount())).intValue());
		OrderEntity.setGiftIntegration(orderItemVo.getPrice().multiply(new BigDecimal(orderItemVo.getCount())).intValue());


		/*价格信息*/
		OrderEntity.setPromotionAmount(new BigDecimal("0.0"));
		OrderEntity.setCouponAmount(new BigDecimal("0.0"));
		OrderEntity.setIntegrationAmount(new BigDecimal("0.0"));
		BigDecimal total = OrderEntity.getSkuPrice().multiply(new BigDecimal(OrderEntity.getSkuQuantity().toString()));
		/*减去优惠信息*/
		BigDecimal subtract = total.subtract(OrderEntity.getPromotionAmount()).subtract(OrderEntity.getCouponAmount()).subtract(OrderEntity.getIntegrationAmount());
		OrderEntity.setRealAmount(subtract);


		return OrderEntity;
	}

	/**
	 * //TODO
	 *
	 * @param null
	 * @return: null
	 * @Description: 获取订单状态 根据订单号获取订单
	 */
	@Override
	public OrderEntity getOrderByOrderSn(String orderSn) {

		return this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
	}

	/**
	 * //TODO
	 *
	 * @param orderEntity
	 * @return: null
	 * @Description: 关闭订单
	 */
	@Override
	public void closeOrder(OrderEntity orderEntity) {
		// 查询当前订单状态
		OrderEntity order = this.getById(orderEntity.getId());
		// 如果是待付款 那么订单付款超时了 关闭
		if (order.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())) {
			OrderEntity order1 = new OrderEntity();
			order1.setId(order.getId());
			order1.setStatus(OrderStatusEnum.CANCLED.getCode());
			/*修改订单状态为已关闭 订单解锁成功*/
			this.updateById(order1);
			// 手动解锁库存 防止网络延迟造成订单与库存时间差（晚收到订单消息 未解锁库存）  库存未解锁
			OrderTo orderTo = new OrderTo();
			BeanUtils.copyProperties(orderEntity, orderTo);
			try {
				// 保证消息百分百发送出去 确定消息到达队列
				rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTo);
				// 做好数据库日志记录
				// 1、定期扫描数据库 将未发送的重新发送
			} catch (Exception e) {
				e.printStackTrace();
				// TODO 将没发送成功发送的消息重试发送
			}
		}
	}

	// 获取支付信息
	@Override
	public PayVo getOrderPayInfo(String orderSn) {

		// 订单
		OrderEntity orderByOrderSn = this.getOrderByOrderSn(orderSn);

		// 过期无法生成支付信息
		if (orderByOrderSn.getStatus().intValue() != OrderStatusEnum.CREATE_NEW.getCode()) {
			return null;
		}
		// 订单项
		List<OrderItemEntity> order_sn = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
		PayVo payVo = new PayVo();
		payVo.setBody(order_sn.get(0).getSkuAttrsVals()); //订单备注
		payVo.setOut_trade_no(orderByOrderSn.getOrderSn()); //订单号
		payVo.setSubject(order_sn.get(0).getSkuName()); //订单标题
		/*设置订单金额小数点位数 以及超出后向上取值*/
		BigDecimal bigDecimal = orderByOrderSn.getPayAmount().setScale(2, RoundingMode.UP);
		payVo.setTotal_amount(bigDecimal.toString()); //订单金额
		return payVo;
	}

	@Override
	public PageUtils queryPageWithItem(Map<String, Object> params) {
		// 获取当前用户信息
		MemberRespVo memberRespVo = LoginUserInterceptor.threadLocal.get();

		IPage<OrderEntity> page = this.page(
				new Query<OrderEntity>().getPage(params),
				new QueryWrapper<OrderEntity>().eq("member_id", memberRespVo.getId()).orderByDesc("id")
		);

		List<OrderEntity> collect = page.getRecords().stream().peek(order -> {
			List<OrderItemEntity> list = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
			order.setItemEntities(list);
		}).collect(Collectors.toList());

		page.setRecords(collect);

		return new PageUtils(page);
	}

	/**
	 * //TODO
	 *
	 * @param payAsyncVo
	 * @return: null
	 * @Description: 处理支付结果数据
	 */
	@Override
	public String handlePayResult(PayAsyncVo payAsyncVo) {
		// 保存交易流水
		PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
		paymentInfoEntity.setAlipayTradeNo(payAsyncVo.getTrade_no());
		paymentInfoEntity.setOrderSn(payAsyncVo.getOut_trade_no());
		paymentInfoEntity.setPaymentStatus(payAsyncVo.getTrade_status());
		paymentInfoEntity.setCallbackTime(payAsyncVo.getNotify_time());
		paymentInfoService.save(paymentInfoEntity);

		// 修改订单状态
		switch (payAsyncVo.getTrade_status()) {
			case "TRADE_SUCCESS":
			case "TRADE_FINISHED":
				this.updateOrderStatus(payAsyncVo.getOut_trade_no(), OrderStatusEnum.PAYED.getCode());
				break;
			default:
				// TODO 其余订单状态
				log.warn("无需修改订单状态");
		}
		return "success";
	}

	// 创建秒杀单信息
	@Transactional
	@Override
	public void createSeckillOrder(SeckillOrderTo seckillOrderTo) {
		// TODO 保存订单信息
		OrderEntity order = new OrderEntity();
		order.setOrderSn(seckillOrderTo.getOrderSn());
		order.setMemberId(seckillOrderTo.getMemberId());
		order.setPayAmount(seckillOrderTo.getSeckillPrice());
		order.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
		order.setCreateTime(new Date());
		this.save(order);
		// 保存订单项
		OrderItemEntity orderItemEntity = new OrderItemEntity();
		orderItemEntity.setOrderSn(order.getOrderSn());
		orderItemEntity.setRealAmount(seckillOrderTo.getSeckillPrice());
		orderItemEntity.setSkuQuantity(seckillOrderTo.getNum());
		// TODO 获取sku详细信息
		orderItemService.save(orderItemEntity);
		// 订单创建成功 发送消息到mq 处理支付逾期
		rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order);
	}

	/**
	 * //TODO
	 *
	 * @param out_trade_no
	 * @param code
	 * @return: boolean
	 * @Description: 根据订单号 修改订单状态
	 */
	private boolean updateOrderStatus(String out_trade_no, Integer code) {
		return this.update(new UpdateWrapper<OrderEntity>().eq("order_sn", out_trade_no).set("status", code));
	}
}