package com.lsh.gulimall.search.feign;

import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.search.feign.impl.ProductFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Primary
@FeignClient(name = "gulimall-product", fallback = ProductFeignClientImpl.class)
public interface ProductFeignClient {

	/**
	 * 信息
	 */
	@GetMapping("/product/attr/info/{attrId}")
	// @RequiresPermissions("product:attr:info")
	R info(@PathVariable("attrId") Long attrId);

}
