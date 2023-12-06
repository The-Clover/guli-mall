package com.lsh.gulimall.product.feign.impl;

import com.lsh.gulimall.common.to.es.SkuEsModel;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.product.feign.SearchFeignClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchFeignClientImpl implements SearchFeignClient {
	@Override
	public R productStatusUp(List<SkuEsModel> skuEsModelList) {
		return R.error("商品上架失败!");
	}
}
