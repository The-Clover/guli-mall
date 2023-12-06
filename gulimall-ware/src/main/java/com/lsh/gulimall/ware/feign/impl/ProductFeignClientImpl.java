package com.lsh.gulimall.ware.feign.impl;


import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.ware.feign.ProductFeignClient;
import org.springframework.stereotype.Component;

@Component
public class ProductFeignClientImpl implements ProductFeignClient {

	@Override
	public R info(Long skuId) {
		return R.error("服务调用失败");
	}
}
