package com.lsh.gulimall.seckill.feign;

import com.lsh.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-product")
@RequestMapping("product/skuinfo")
public interface ProductFeignClient {
	/**
	 * 信息
	 */
	@RequestMapping("/info/{skuId}")
	R getSkuInfo(@PathVariable("skuId") Long skuId);
}
