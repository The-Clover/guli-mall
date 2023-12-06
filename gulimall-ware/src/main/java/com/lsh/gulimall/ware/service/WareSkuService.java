package com.lsh.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.to.SkuHasStockTo;
import com.lsh.gulimall.common.to.mq.OrderTo;
import com.lsh.gulimall.common.to.mq.StockLockedTo;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.ware.entity.WareSkuEntity;
import com.lsh.gulimall.ware.entity.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:36:48
 */
public interface WareSkuService extends IService<WareSkuEntity> {

	PageUtils queryPage(Map<String, Object> params);

	boolean addStock(Long skuId, Long wareId, Integer skuNum);

	List<SkuHasStockTo> hasStock(List<Long> skuId);

	Boolean orderLockStock(WareSkuLockVo wareSkuLockVo);

	void unlockStockByOrder(StockLockedTo lockedTo);

	void unlockStockByOrder(OrderTo order);
}

