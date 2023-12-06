package com.lsh.gulimall.product.feign;


import com.lsh.gulimall.common.to.SkuReductionTo;
import com.lsh.gulimall.common.to.SpuBoundTo;
import com.lsh.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;

// @Primary  解决 idea中 fallback 实现类 FeignClient 重复问题 : Could not autowire. There is more than one bean
@Primary
@FeignClient(name = "gulimall-gateway")
public interface CouponFeignClient {

	@PostMapping("/api/coupon/spubounds/saveSpuBounds")
	R saveSpuBounds(SpuBoundTo spuBoundTo);

	@PostMapping("/api/coupon/skufullreduction/save/reduction")
	R saveSkuRedution(SkuReductionTo skuReductionTo);

}
