package com.lsh.gulimall.ware.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class MergeVo {

	private List<Long> items;
	private Long purchaseId;
}
