package com.lsh.gulimall.product.feign.impl;

import com.lsh.gulimall.common.to.SkuHasStockTo;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.product.feign.WareFeignClient;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class WareFeignClientImpl implements WareFeignClient {

	@Override
	public R haStock(List<Long> skuId) {
		return R.error("服务调用失败!");
	}
}
