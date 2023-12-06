package com.lsh.gulimall.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillOrderTo {

	private String orderSn; // 订单号

	private Long promotionSessionId;  // 活动场次id

	private Long skuId; // 商品id

	private BigDecimal seckillPrice; // 秒杀价格

	private Integer num; //数量

	private Long memberId; // 用户id

}
