package com.lsh.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.lsh.gulimall.common.to.es.SkuEsModel;
import com.lsh.gulimall.search.config.ESConfig;
import com.lsh.gulimall.search.constant.EsConstant;
import com.lsh.gulimall.search.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Override
	public boolean productStatusUp(List<SkuEsModel> skuEsModelList) {
		/*1.批量保存数据*/
		BulkRequest bulkRequest = new BulkRequest();
		for (SkuEsModel skuEsModel : skuEsModelList) {
			/*指定索引*/
			IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
			/*指定id*/
			indexRequest.id(skuEsModel.getSkuId().toString());
			String string = JSON.toJSONString(skuEsModel);
			/*保存数据 指定类型*/
			indexRequest.source(string, XContentType.JSON);
			bulkRequest.add(indexRequest);
		}
		try {
			BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, ESConfig.COMMON_OPTIONS);

			// TODO 出现错误
			if (bulk.hasFailures()) {
				/*获取上架失败的商品id*/
				List<String> collect = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
				log.error("保存出现错误! {}", collect);
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}
