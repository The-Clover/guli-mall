package com.lsh.gulimall.search.vo;

import com.lsh.gulimall.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * //TODO
 *
 * @Author shihe
 * @Date 22:49 2021/7/8
 * @Description 查询返回结果
 **/
@Data
public class SearchResult {
	/*商品信息*/
	private List<SkuEsModel> products;

	/*分页信息*/
	private Integer pageNum;
	private Long total;
	private Integer totalPages;

	/*涉及品牌*/
	private List<BrandVo> brands;

	/*涉及分类数据*/
	private List<CatalogVo> catalogs;
	/*属性信息*/
	private List<AttrVo> attrs;
	private List<Integer> pageNavs;

	private List<NavVo> navs;


	@Data
	public static class NavVo {
		private String navName;
		private String navValue;
		private String link;
	}


	@Data
	public static class BrandVo {
		private Long brandId;
		private String brandName;
		private String brandImg;
	}

	@Data
	public static class AttrVo {
		private Long attrId;
		private String attrName;
		private List<String> attrValue;
	}

	@Data
	public static class CatalogVo {
		private Long catalogId;
		private String catalogName;
	}
}
