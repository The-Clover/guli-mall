package com.lsh.gulimall.order.vo;

import com.lsh.gulimall.order.entity.OrderEntity;
import lombok.Data;

@Data
public class SubmitOrderResponseVo {
	private OrderEntity order;
	private Integer code; //0 成功
}
