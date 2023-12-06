package com.lsh.gulimall.ware.feign;


import com.lsh.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "gulimall-order")
@RequestMapping("order/order")
public interface OrderFeignClient {
	/**
	 * 订单详细信息
	 */
	@GetMapping("status/{orderSn}")
	R orderStatus(@PathVariable("orderSn") String orderSn);
}
