package com.lsh.gulimall.ware.entity.vo;

import lombok.Data;

@Data
public class LockStockResult {
	private Long skuId;
	private Integer num;
	private Boolean loched;
}
