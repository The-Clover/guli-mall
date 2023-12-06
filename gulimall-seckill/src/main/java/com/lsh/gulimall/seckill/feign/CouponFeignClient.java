package com.lsh.gulimall.seckill.feign;

import com.lsh.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-coupon")
@RequestMapping("coupon/seckillsession")
public interface CouponFeignClient {


	/**
	 * 获取需要秒杀的商品 3天
	 */
	@RequestMapping("/latest3DaySession")
	R getLatest3DaySession();

}
