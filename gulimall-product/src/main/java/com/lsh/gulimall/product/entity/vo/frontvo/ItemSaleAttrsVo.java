package com.lsh.gulimall.product.entity.vo.frontvo;


import lombok.Data;

import java.util.List;

@Data
public class ItemSaleAttrsVo {
	/**
	 * 属性id
	 */
	private Long attrId;
	/**
	 * 属性名
	 */
	private String attrName;


	private List<AttrValueWithSkuIdVo> attrValues;
}