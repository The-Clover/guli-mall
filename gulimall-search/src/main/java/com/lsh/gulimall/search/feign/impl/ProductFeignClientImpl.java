package com.lsh.gulimall.search.feign.impl;

import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.search.feign.ProductFeignClient;
import org.springframework.stereotype.Component;

@Component
public class ProductFeignClientImpl implements ProductFeignClient {
	@Override
	public R info(Long attrId) {
		return R.error("服务调用失败!");
	}
}
