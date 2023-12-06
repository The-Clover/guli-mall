package com.lsh.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lsh.gulimall.common.to.es.SkuEsModel;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.search.config.ESConfig;
import com.lsh.gulimall.search.constant.EsConstant;
import com.lsh.gulimall.search.feign.ProductFeignClient;
import com.lsh.gulimall.search.service.MallSearchService;
import com.lsh.gulimall.search.vo.AttrResponseVo;
import com.lsh.gulimall.search.vo.SearchParam;
import com.lsh.gulimall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MallSearchServiceIml implements MallSearchService {

	@Autowired
	RestHighLevelClient restHighLevelClient;


	@Autowired
	ProductFeignClient productFeignService;

	@Override
	public SearchResult search(SearchParam searchParam) {

		SearchResult searchResult = null;


		/*准备检索请求*/
		SearchRequest searchRequest = buildSearchRequest(searchParam);
		try {
			/*执行请求*/
			SearchResponse response = restHighLevelClient.search(searchRequest, ESConfig.COMMON_OPTIONS);
			/*封装结果*/
			searchResult = buildSearchResult(response, searchParam);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return searchResult;
	}

	/*封装返回结果*/
	private SearchResult buildSearchResult(SearchResponse response, SearchParam searchParam) {
		SearchResult searchResult = new SearchResult();
		List<SkuEsModel> skuEsModels = new ArrayList<>();
		/*1 返回所有查询到的商品*/
		SearchHit[] productHits = response.getHits().getHits();

		if (productHits != null && productHits.length > 0) {
			for (SearchHit productHit : productHits) {
				String sourceAsString = productHit.getSourceAsString();
				/*解析商品对象*/
				SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
				if (!StringUtils.isEmpty(searchParam.getKeyword())) {
					/*高亮*/
					esModel.setSkuTitle(productHit.getHighlightFields().get("skuTitle").getFragments()[0].string());
				}
				skuEsModels.add(esModel);
			}
			searchResult.setProducts(skuEsModels);
		}

		/*2. 所有商品涉及的所有属性信息*/
		/*所有聚合信息*/
		Aggregations aggregations = response.getAggregations();
		ArrayList<SearchResult.AttrVo> attrVos = new ArrayList<>();
		ParsedNested attr_agg = aggregations.get("attr_agg");
		Aggregations aggregations2 = attr_agg.getAggregations();
		/*id*/
		ParsedLongTerms attr_id_agg = aggregations2.get("attr_id_agg");
		List<? extends Terms.Bucket> buckets1 = attr_id_agg.getBuckets();
		if (buckets1 != null && buckets1.size() > 0) {
			for (Terms.Bucket bucket : buckets1) {
				SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
				long attrId = bucket.getKeyAsNumber().longValue();
				/*属性id*/
				attrVo.setAttrId(attrId);
				Aggregations aggregations1 = bucket.getAggregations();
				ParsedStringTerms attr_name_agg = aggregations1.get("attr_name_agg");
				String attrName = attr_name_agg.getBuckets().get(0).getKeyAsString();
				/*属性名*/
				attrVo.setAttrName(attrName);

				ParsedStringTerms attr_value_agg = aggregations1.get("attr_value_agg");
				List<String> attrValues = attr_value_agg.getBuckets().stream().map(bucket1 -> {
					String asString = bucket1.getKeyAsString();
					return asString;
				}).collect(Collectors.toList());
				/*属性值*/
				attrVo.setAttrValue(attrValues);
				attrVos.add(attrVo);
			}
			searchResult.setAttrs(attrVos);
		}



		/*3. 当前商品所涉及的所有品牌信息*/
		ParsedLongTerms brand_agg = aggregations.get("brand_agg");
		ArrayList<SearchResult.BrandVo> brandVos = new ArrayList<>();
		List<? extends Terms.Bucket> buckets = brand_agg.getBuckets();
		if (buckets != null && buckets.size() > 0) {
			for (Terms.Bucket bucket : buckets) {
				SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
				/*品牌id*/
				brandVo.setBrandId(Long.valueOf(bucket.getKeyAsString()));
				/*品牌名*/
				ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
				String brandName = brand_name_agg.getBuckets().get(0).getKeyAsString();
				brandVo.setBrandName(brandName);

				/*品牌图片*/
				ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
				String brandImg = brand_img_agg.getBuckets().get(0).getKeyAsString();
				brandVo.setBrandImg(brandImg);

				brandVos.add(brandVo);
			}
			searchResult.setBrands(brandVos);
		}

		/*4. 分类信息*/
		ParsedLongTerms catalog_agg = aggregations.get("catalog_agg");
		List<SearchResult.CatalogVo> catalogVoList = new ArrayList<>();
		if (catalog_agg.getBuckets() != null && catalog_agg.getBuckets().size() > 0) {
			for (Terms.Bucket bucket : catalog_agg.getBuckets()) {
				SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
				/*id*/
				catalogVo.setCatalogId(Long.valueOf(bucket.getKeyAsString()));
				Aggregations aggregations1 = bucket.getAggregations();
				ParsedStringTerms catalogNameAgg = aggregations1.get("catalog_name_agg");
				String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
				/*catalog name*/
				catalogVo.setCatalogName(catalogName);
				catalogVoList.add(catalogVo);
			}
			searchResult.setCatalogs(catalogVoList);
		}

		/*5. 分页信息*/
		long total = response.getHits().getTotalHits().value;
		/*当前页码*/
		searchResult.setPageNum(searchParam.getPageNum());
		searchResult.setTotal(total);
		/*总页码*/
		searchResult.setTotalPages((int) (total % EsConstant.PRODUCT_PAGESIZE == 0 ? (total / EsConstant.PRODUCT_PAGESIZE) : (total / EsConstant.PRODUCT_PAGESIZE + 1)));
		System.out.println("searchResult = " + JSON.toJSONString(searchResult));

		List<Integer> pageNavs = new ArrayList<>();
		for (int i = 0; i < searchResult.getTotalPages(); i++) {
			pageNavs.add(i + 1);
		}

		searchResult.setPageNavs(pageNavs);

		/*构建面包屑导航*/
		List<String> attrs = searchParam.getAttrs();
		if (attrs != null && attrs.size() > 0) {
			List<SearchResult.NavVo> navVos = attrs.stream().map(attr -> {
				String[] split = attr.split("_");
				SearchResult.NavVo navVo = new SearchResult.NavVo();
				//6.1 设置属性值
				navVo.setNavValue(split[1]);
				//6.2 查询并设置属性名
				try {
					R r = productFeignService.info(Long.parseLong(split[0]));
					System.out.println(r);
					if (r.getCode() == 0) {
						AttrResponseVo attrResponseVo = JSON.parseObject(JSON.toJSONString(r.get("attr")), new TypeReference<AttrResponseVo>() {
						});
						navVo.setNavName(attrResponseVo.getAttrName());
					}
				} catch (Exception e) {
					log.error("远程调用商品服务查询属性失败", e);
				}
				//6.3 设置面包屑跳转链接(当点击该链接时剔除点击属性)
				String queryString = searchParam.get_queryString();
				String replace = queryString.replace("&attrs=" + attr, "").replace("attrs=" + attr + "&", "").replace("attrs=" + attr, "");
				navVo.setLink("http://search.gulimall.com/search.html" + (replace.isEmpty() ? "" : "?" + replace));
				return navVo;
			}).collect(Collectors.toList());
			searchResult.setNavs(navVos);
		}

		// TODO brand 面包屑导航


		return searchResult;
	}

	/*封装检索请求*/
	private SearchRequest buildSearchRequest(SearchParam searchParam) {
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		/*模糊匹配,过滤(属性,分类,品牌,价格区间,库存)*/
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//		1.must
		if (!StringUtils.isEmpty(searchParam.getKeyword())) {
			boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyword()));
		}
//		1.2.1 filter catalogId
		if (searchParam.getCatalog3Id() != null) {
			boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", searchParam.getCatalog3Id()));
		}
//		1.2.2 filter brandId
		if (searchParam.getBrandId() != null && searchParam.getBrandId().size() > 0) {
			boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", searchParam.getBrandId()));
		}
//		1.2.3 filter attrs
		if (searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0) {
			List<String> attrs = searchParam.getAttrs();
			for (String attr : attrs) {
				BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
				String[] s = attr.split("_");
				/*attrId 属性id*/
//				s[0]
				/*attrValue 多个属性value*/
				String[] split = s[1].split(":");
				/*属性id*/
				queryBuilder.must(QueryBuilders.termQuery("attrs.attrId", s[0]));
				/*属性值*/
				queryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", split));

				/*每一个都需要生成一个 nestedQuery 并且放入 boolQueryBuilder.filter*/
				NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", queryBuilder, ScoreMode.None);
				/*嵌入式*/
				boolQueryBuilder.filter(nestedQuery);
			}
		}
//		1.2.4 filter hasStock
		if (searchParam.getHasStock() != null) {
			boolQueryBuilder.filter(QueryBuilders.termsQuery("hasStock", searchParam.getHasStock() == 1));
		}
//		1.2.5 filter skuPrice
		if (!StringUtils.isEmpty(searchParam.getSkuPrice())) {

			RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("skuPrice");
			String skuPrice = searchParam.getSkuPrice();
			String[] split = skuPrice.split("_");
			if (split.length == 2) {
				rangeQueryBuilder.gte(split[0]);
				rangeQueryBuilder.lte(split[1]);
			} else if (skuPrice.startsWith("_")) {
				rangeQueryBuilder.gte(0);
				rangeQueryBuilder.lte(split[0]);
			} else {
				rangeQueryBuilder.gte(split[0]);
			}
			boolQueryBuilder.filter(rangeQueryBuilder);
		}
		searchSourceBuilder.query(boolQueryBuilder);

		/*
		 * 聚合分析
		 * */

		/*排序*/
		if (!StringUtils.isEmpty(searchParam.getSort())) {
			String sort = searchParam.getSort();
			String[] s = sort.split("_");
			/*排序字段*/
//			s[0]
			/*排序方式*/
//			s[1]
			searchSourceBuilder.sort(s[0], s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC);
		}
		/*分页*/
//		PRODUCT_PAGESIZE
		/*-1*页面大小*/
		searchSourceBuilder.from((searchParam.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
		searchSourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

		/*高亮*/
		if (!StringUtils.isEmpty(searchParam.getKeyword())) {
			HighlightBuilder highlightBuilder = new HighlightBuilder();
			highlightBuilder.field("skuTitle");
			highlightBuilder.preTags("<b style='color:red;'>");
			highlightBuilder.postTags("</b>");
			searchSourceBuilder.highlighter(highlightBuilder);
		}
		/*品牌聚合*/
		TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
		/*品牌id*/
		brand_agg.field("brandId").size(50);

		/*子聚合*/
		/*品牌名字*/
		brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName.keyword").size(1));
		/*品牌图片*/
		brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg.keyword").size(1));
//		1
		searchSourceBuilder.aggregation(brand_agg);

		/*分类聚合*/
		TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
		catalog_agg.field("catalogId").size(20);
		catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("cataName.keyword").size(1));

//		2
		searchSourceBuilder.aggregation(catalog_agg);

		/*属性聚合 嵌入式*/
		NestedAggregationBuilder nested = AggregationBuilders.nested("attr_agg", "attrs");
		/*子聚合*/
		TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
		/*子子*/
		TermsAggregationBuilder attr_name_agg = AggregationBuilders.terms("attr_name_agg").field("attrs.attrName.keyword").size(1);
		TermsAggregationBuilder attr_value_agg = AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50);

		attr_id_agg.subAggregation(attr_name_agg);
		attr_id_agg.subAggregation(attr_value_agg);

		nested.subAggregation(attr_id_agg);

//		3
		searchSourceBuilder.aggregation(nested);

		System.out.println("构建的DSL searchSourceBuilder = " + searchSourceBuilder);
		SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, searchSourceBuilder);
		return searchRequest;
	}

}
