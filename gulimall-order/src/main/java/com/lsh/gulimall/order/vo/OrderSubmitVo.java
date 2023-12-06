package com.lsh.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * //TODO
 *
 * @Description: 订单确认页提交的数据
 * @Author: shihe
 * @Date: 2021-09-24 07:20
 */
@Data
public class OrderSubmitVo {

	/*收货地址*/
	private Long addrId;
	/*支付方式*/
	private Integer payType;

	//无需提交需要购买的商品 重新从购物车获取即可

	//优惠 发票。。。

	/*防重令牌*/
	private String orderToken;

	/*页面价格与后台总价对比*/
	private BigDecimal payPrice;

	// 用户信息都在session中

	/*订单备注*/
	private String note;
}