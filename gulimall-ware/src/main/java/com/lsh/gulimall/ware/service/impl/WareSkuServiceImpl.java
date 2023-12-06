package com.lsh.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.exception.NoStockException;
import com.lsh.gulimall.common.to.SkuHasStockTo;
import com.lsh.gulimall.common.to.mq.OrderTo;
import com.lsh.gulimall.common.to.mq.StockDetailTo;
import com.lsh.gulimall.common.to.mq.StockLockedTo;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.ware.dao.WareSkuDao;
import com.lsh.gulimall.ware.entity.WareOrderTaskDetailEntity;
import com.lsh.gulimall.ware.entity.WareOrderTaskEntity;
import com.lsh.gulimall.ware.entity.WareSkuEntity;
import com.lsh.gulimall.ware.entity.vo.OrderItemVo;
import com.lsh.gulimall.ware.entity.vo.OrderVo;
import com.lsh.gulimall.ware.entity.vo.WareSkuLockVo;
import com.lsh.gulimall.ware.feign.OrderFeignClient;
import com.lsh.gulimall.ware.feign.ProductFeignClient;
import com.lsh.gulimall.ware.service.WareOrderTaskDetailService;
import com.lsh.gulimall.ware.service.WareOrderTaskService;
import com.lsh.gulimall.ware.service.WareSkuService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
@Slf4j
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


	@Autowired
	private ProductFeignClient productFeignClient;

	@Autowired
	WareSkuDao wareSkuDao;

	@Autowired
	RabbitTemplate rabbitTemplate;

	@Autowired
	WareOrderTaskService wareOrderTaskService;
	@Autowired
	WareOrderTaskDetailService wareOrderTaskDetailService;

	@Autowired
	OrderFeignClient orderFeignClient;

	@Autowired
	StringRedisTemplate redisTemplate;

	@Override
	public void unlockStockByOrder(StockLockedTo lockedTo) {
		StockDetailTo detailTo = lockedTo.getDetailTo();
		/*先去数据库查询有没有这条工作单 有：库存锁定成功 其他服务出现问题 没有：库存本身锁定失败 已经回滚 无需处理（解锁）*/
		Long detailId = detailTo.getId();
		WareOrderTaskDetailEntity taskDetailEntity = wareOrderTaskDetailService.getById(detailId);
		if (taskDetailEntity != null) {
			/*数据库存在详情单 如果整个订单失败：说明是其他服务出现问题 或者 支付过期（超时未支付，手动取消） 全部库存已经锁定成功 解锁 | 如果整个订单成功：无需解锁*/
			// 远程查询订单情况
			Long taskId = lockedTo.getId(); // 库存工作单Id
			WareOrderTaskEntity wareOrderTask = wareOrderTaskService.getById(taskId);
			/*订单号*/
			String orderSn = wareOrderTask.getOrderSn();
			R r = orderFeignClient.orderStatus(orderSn);
			if (r.getCode() == 0) {
				OrderVo orderVo = r.getData(new TypeReference<OrderVo>() {
				});
				/*获取订单状态*/
				if (orderVo == null || orderVo.getStatus() == 4 || orderVo.getStatus() == 0) {
					/*订单不存在 其他非库存服务发生问题 事务已经回到滚 解锁*/
					/*库存工作单 未解锁 即解锁 1（解锁状态：未解锁）*/
					if (taskDetailEntity.getLockStatus() == 1) {
						unLockStock(detailTo.getSkuId(), detailTo.getWareId(), detailTo.getSkuNum(), detailTo.getId());
					}
				}
			} else {
				/*拒绝 true 重新放到队列 让别人继续消费解锁*/
				log.warn("远程调用失败,可能未解锁库存,等待下次解锁");
				/*远程服务失败*/
				throw new RuntimeException("远程服务调用失败");
			}
		}
	}

	@Override
	@Transactional
	public void unlockStockByOrder(OrderTo order) {
		WareOrderTaskEntity wareOrderTask = wareOrderTaskService.getOrderTaskByOrderSn(order.getOrderSn());
		if (wareOrderTask == null) {
			// TODO 增加信号量
			log.warn("秒杀商品, 无需修改数据库库存!");
		} else {    // 找到未解锁的库存
			List<WareOrderTaskDetailEntity> wareOrderTaskDetailEntityList = wareOrderTaskDetailService.getlockOrderTaskDetailByOrderTaskId(wareOrderTask.getId());
			List<WareOrderTaskDetailEntity> collect = wareOrderTaskDetailEntityList.stream().peek(wareOrderTaskDetailEntity -> {
						// 解锁
						wareSkuDao.unlockStock(wareOrderTaskDetailEntity.getSkuId(), wareOrderTaskDetailEntity.getWareId(), wareOrderTaskDetailEntity.getSkuNum());
						wareOrderTaskDetailEntity.setLockStatus(2);
					}
			).collect(Collectors.toList());
			if (collect != null && collect.size() > 0) {
				// 修改库存锁定详情单锁定状态
				wareOrderTaskDetailService.updateBatchById(collect);
			}
		}
	}

	public void unLockStock(long skuId, Long wareId, Integer num, long taskDetailId) {
		/*解锁*/
		wareSkuDao.unlockStock(skuId, wareId, num);
		/*更新库存工作单*/
		WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity();
		wareOrderTaskDetailEntity.setId(taskDetailId);
		/*修改解锁状态为2（已解锁）*/
		wareOrderTaskDetailEntity.setLockStatus(2);
		wareOrderTaskDetailService.updateById(wareOrderTaskDetailEntity);
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params) {

		/*排序字段*/
		String sidx = (String) params.get("sidx");
		/*排序方式*/
		String order = (String) params.get("order");
		String wareId = (String) params.get("wareId");
		String skuId = (String) params.get("skuId");

		QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();


		if (!StringUtils.isEmpty(skuId)) {
			wrapper.eq("sku_id", skuId);
		}

		if (!StringUtils.isEmpty(wareId)) {
			wrapper.eq("ware_id", wareId);
		}

		if ("desc".equals(order) && !StringUtils.isEmpty(sidx)) {
			wrapper.orderByDesc(sidx);
		} else if (!StringUtils.isEmpty(sidx)) {
			wrapper.orderByAsc(sidx);
		}

		IPage<WareSkuEntity> page = this.page(
				new Query<WareSkuEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Transactional
	@Override
	public boolean addStock(Long skuId, Long wareId, Integer skuNum) {

		if (this.count(new QueryWrapper<WareSkuEntity>().eq("ware_id", wareId).eq("sku_id", skuId)) == 0) {
			WareSkuEntity wareSkuEntity = new WareSkuEntity();
			wareSkuEntity.setSkuId(skuId);
			wareSkuEntity.setWareId(wareId);
			wareSkuEntity.setStock(skuNum);
			wareSkuEntity.setStockLocked(0);

			try {

				R r = productFeignClient.info(skuId);
				Map<String, Object> info = (Map<String, Object>) r.get("skuInfo");
				if (r.getCode() == 0) {
					wareSkuEntity.setSkuName((String) info.get("skuName"));
				}
			} catch (Exception ignored) {
				/*手动处理异常*/

				// TODO  让事务不回滚
			}
			this.save(wareSkuEntity);
		} else {
			baseMapper.addStock(skuId, wareId, skuNum);
		}
		return true;
	}

	@Override
	public List<SkuHasStockTo> hasStock(List<Long> skuIds) {
		List<WareSkuEntity> skuEntityList = this.list(new QueryWrapper<WareSkuEntity>().in("sku_id", skuIds));

		List<SkuHasStockTo> skuHasStockVoList = skuEntityList.stream().map(wareSkuEntity -> {
			SkuHasStockTo skuHasStockVo = new SkuHasStockTo();
			skuHasStockVo.setSkuId(wareSkuEntity.getSkuId());
			/*有库存: 库存减去锁定库存大于0*/
			if (wareSkuEntity.getSkuId() != null && wareSkuEntity.getStock() != null && wareSkuEntity.getStockLocked() != null) {
				skuHasStockVo.setHasStock(wareSkuEntity.getStock() - wareSkuEntity.getStockLocked() > 0);
			} else {
				skuHasStockVo.setHasStock(false);
			}
			return skuHasStockVo;
		}).collect(Collectors.toList());

		return skuHasStockVoList;
	}

	/**
	 * //TODO
	 *
	 * @param vo
	 * @return: List<LockStockResult>
	 * @Description: 为某个订单锁定库存
	 * Transactional(rollbackFor = NoStockException.class) 默认只要是运行时异常都会回滚
	 * * 解锁的条件
	 * * 1、下订单成功，过期没有支付
	 * * 2、手动取消订单
	 * * 3、下订单成功，锁定库存成功，之后的其他业务失败了
	 */
	@Override
	@Transactional(rollbackFor = NoStockException.class)
	public Boolean orderLockStock(WareSkuLockVo vo) {
		// 1、按照收获地址 找到就近仓库 锁定库存

		//订单库存工作单
		WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
		wareOrderTaskEntity.setOrderSn(vo.getOrderSn());
		wareOrderTaskService.save(wareOrderTaskEntity);

		/*找到每个商品在哪个仓库都有库存*/
		List<OrderItemVo> locks = vo.getLocks();
		List<SkuWareHasStock> wareHasStockList = locks.stream().map(item -> {
			SkuWareHasStock skuWareHasStock = new SkuWareHasStock();
			Long skuId = item.getSkuId();
			skuWareHasStock.setSkuId(skuId);
			skuWareHasStock.setNum(item.getCount());
			/*查询在哪里有库存*/
			List<Long> wareIdList = wareSkuDao.listWareIdHasSkuStock(skuId);
			if (wareIdList != null && wareIdList.size() > 0) {
				skuWareHasStock.setWareId(wareIdList);
			}
			return skuWareHasStock;
		}).collect(Collectors.toList());
		Boolean allLock = true;
		for (SkuWareHasStock skuWareHasStock : wareHasStockList) {

			Boolean skuStocked = false;

			Long skuId = skuWareHasStock.getSkuId();
			List<Long> wareId = skuWareHasStock.getWareId();
			/*无库存*/
			if (wareId == null || wareId.size() == 0) {
				throw new NoStockException(skuId);
			}
			for (Long id : wareId) {
				Long count = wareSkuDao.lockSkuStock(skuId, id, skuWareHasStock.getNum());
				if (count != 0L) {
					/*锁库存成功*/
					skuStocked = true;
					/*库存工作详情单 1 锁定成功*/
					WareOrderTaskDetailEntity wareOrderTaskDetailEntity = new WareOrderTaskDetailEntity(null, skuId, "", skuWareHasStock.getNum(), wareOrderTaskEntity.getId(), id, 1);
					wareOrderTaskDetailService.save(wareOrderTaskDetailEntity);
					/*发送消息(库存工作单) 库存锁定成功*/
					StockLockedTo stockLockedTo = new StockLockedTo();
					/*工作单id*/
					stockLockedTo.setId(wareOrderTaskEntity.getId());
					/*详情单全部信息 防止回滚以后找不到数据*/
					StockDetailTo stockDetailTo = new StockDetailTo();
					BeanUtils.copyProperties(wareOrderTaskDetailEntity, stockDetailTo);
					stockLockedTo.setDetailTo(stockDetailTo);
					rabbitTemplate.convertAndSend("stock-event-exchange", "stock.locked", stockLockedTo);
					break;
				} else {

				}
			}
			/*未锁成功*/
			if (!skuStocked) {
				throw new NoStockException(skuId);
			}
		}
		// 全部锁定成功
		return true;
	}


	@Data
	class SkuWareHasStock {
		private Long skuId;
		private List<Long> wareId;
		private Integer num;
	}
}