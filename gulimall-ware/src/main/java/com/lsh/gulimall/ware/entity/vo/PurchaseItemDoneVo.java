package com.lsh.gulimall.ware.entity.vo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class PurchaseItemDoneVo {
	@NotNull
	private Long itemId;

	@Min(0)
	@Max(4)
	private Integer status;
	private String reason;
}
