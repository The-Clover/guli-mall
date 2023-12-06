package com.lsh.gulimall.ware.entity.vo;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PurchaseDoneVo {
	@NotNull
	private Long id;

	@Valid
	private List<PurchaseItemDoneVo> item;

}
