package com.lsh.gulimall.product.feign.impl;

import com.lsh.gulimall.common.exception.BizCodeEnume;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.product.feign.SeckillFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SeckillFeignClientImpl implements SeckillFeignClient {
	@Override
	public R getSkuSeckillInfo(Long skuId) {
		log.warn("sec熔断");
		return R.error(BizCodeEnume.TOO_MANY_REQUEST.getCode(), BizCodeEnume.TOO_MANY_REQUEST.getMsg() + "--seckill");
	}
}
