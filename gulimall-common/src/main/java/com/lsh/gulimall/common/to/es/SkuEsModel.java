package com.lsh.gulimall.common.to.es;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


/**
 * //TODO
 *
 * @Author codestar
 * @Date 下午11:31 2021/6/28
 * @Description 上架商品es对象
 **/
@Data
public class SkuEsModel {
	private Long skuId;
	private Long spuId;
	private String skuTitle;
	private BigDecimal skuPrice;
	private String skuImg;

	private Long saleCount;
	private boolean haStock;
	private Long hotScore;
	private Long brandId;
	private Long catalogId;
	private String brandName;
	private String brandImg;
	private String cataName;

	private List<Attr> attrs;

	@Data
	public static class Attr implements Serializable {
		private Long attrId;
		private String attrName;
		private String attrValue;
	}
}
