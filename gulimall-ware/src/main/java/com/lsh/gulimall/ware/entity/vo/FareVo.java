package com.lsh.gulimall.ware.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FareVo {
	private MemberReceiveAddressEntity addressEntity;
	private BigDecimal fare;
}
