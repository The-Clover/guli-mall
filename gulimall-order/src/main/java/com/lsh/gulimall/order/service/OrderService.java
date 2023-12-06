package com.lsh.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.to.mq.SeckillOrderTo;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.order.entity.OrderEntity;
import com.lsh.gulimall.order.vo.*;

import java.rmi.ServerException;
import java.util.Map;

/**
 * 订单
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2021-09-02 02:55:05
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

	OrderConfirmVo confirmOrder() throws ServerException;


	SubmitOrderResponseVo submitOrder(OrderSubmitVo orderSubmitVo);

	OrderEntity getOrderByOrderSn(String orderSn);

	void closeOrder(OrderEntity orderEntity);

	PayVo getOrderPayInfo(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);

    String handlePayResult(PayAsyncVo payAsyncVo);

	void createSeckillOrder(SeckillOrderTo seckillOrderTo);

}

