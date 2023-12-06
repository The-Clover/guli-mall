package com.lsh.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {
	private MemberAddressVo addressEntity;
	private BigDecimal fare;
}
