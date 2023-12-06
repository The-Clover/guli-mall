package com.lsh.cart.feign.impl;

import com.lsh.cart.feign.ProductFeignService;
import com.lsh.gulimall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class ProductFeignServiceImpl implements ProductFeignService {
	@Override
	public R info(Long skuId) {
		log.error("查询购物车商品信息失败 skuId=" + skuId);
		return R.error();
	}

	@Override
	public List<String> getSkuSaleAttrValues(Long skuId) {
		log.error("查询购物车商品attr组合信息失败 skuId=" + skuId);
		return null;
	}

	@Override
	public BigDecimal getPrice(Long skuId) {
		log.error("查询商品最新价格失败 skuId=" + skuId);
		return null;
	}
}
