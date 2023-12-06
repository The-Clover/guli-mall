package com.lsh.gulimall.product.feign;

import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.product.feign.impl.SeckillFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "gulimall-seckill", fallback = SeckillFeignClientImpl.class)
@Primary
public interface SeckillFeignClient {
	/**
	 * //TODO
	 *
	 * @param skuId
	 * @return: R
	 * @Description: 获取商品是否在活动
	 */
	@GetMapping("/sku/seckill/{skuId}")
	R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
