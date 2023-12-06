package com.lsh.gulimall.ware.feign;

import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.ware.feign.impl.ProductFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Primary
@FeignClient(name = "gulimall-gateway", fallback = ProductFeignClientImpl.class)
public interface ProductFeignClient {

	/**
	 * 商品sku信息
	 */
	@RequestMapping("api/product/skuinfo/info/{skuId}")
	// @RequiresPermissions("product:skuinfo:info")
	public R info(@PathVariable("skuId") Long skuId);


}
