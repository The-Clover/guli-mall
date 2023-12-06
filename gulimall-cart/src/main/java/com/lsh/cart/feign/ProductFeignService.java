package com.lsh.cart.feign;

import com.lsh.cart.feign.impl.ProductFeignServiceImpl;
import com.lsh.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Primary
@Service
@FeignClient(name = "gulimall-product", fallback = ProductFeignServiceImpl.class)
public interface ProductFeignService {

	@RequestMapping("product/skuinfo/info/{skuId}")
	R info(@PathVariable("skuId") Long skuId);

	@GetMapping("product/skusaleattrvalue/stringList/{skuId}")
	List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);


	/**
	 * 结算页面最新价格获取
	 */
	@GetMapping("product/skuinfo/{skuId}/price")
	BigDecimal getPrice(@PathVariable("skuId") Long skuId);
}