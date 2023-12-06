package com.lsh.gulimall.order.to;

import com.lsh.gulimall.order.entity.OrderEntity;
import com.lsh.gulimall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateTo {

	private OrderEntity order;

	/*订单项*/
	private List<OrderItemEntity> items;

	/*订单价格*/
	private BigDecimal payPrice;

	/*运费*/
	private BigDecimal fare;
}
