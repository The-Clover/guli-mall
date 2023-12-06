package com.lsh.gulimall.order.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "gulimall-coupon")
public interface CouponService {
}
