package com.lsh.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * //TODO
 *
 * @Author shihe
 * @Date 2:05 2021/7/8
 * @Description 前端参数vo
 **/
@Data
public class SearchParam {
	private String keyword;  //全文匹配
	private Long catalog3Id; //三级分类id
	/*
	 * saleCount_asc/desc
	 * skuPrice
	 * hotScore
	 * */
	private String sort;
	/*是否有货*/
	private Integer hasStock;
	/*价格区间*/
	private String skuPrice;
	/*品牌*/
	private List<Long> brandId;
	/*属性*/
	private List<String> attrs;
	/*页码*/
	private Integer pageNum=1;
	/*查询字符串*/
	private String _queryString;
}
