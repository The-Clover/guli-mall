package com.lsh.gulimall.product.feign;

import com.lsh.gulimall.common.exception.BizCodeEnume;
import com.lsh.gulimall.common.to.es.SkuEsModel;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.product.feign.impl.SearchFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Primary
@FeignClient(value = "gulimall-search", fallback = SearchFeignClientImpl.class)
public interface SearchFeignClient {

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/6/29 上午2:06
	 * @Description 上架商品
	 */
	@PostMapping("/search/save/product")
	R productStatusUp(@RequestBody List<SkuEsModel> skuEsModelList);
}

