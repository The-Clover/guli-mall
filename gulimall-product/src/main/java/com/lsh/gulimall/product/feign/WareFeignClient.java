package com.lsh.gulimall.product.feign;

import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.product.feign.impl.WareFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author shihe
 */
@Primary
@FeignClient(name = "gulimall-ware", fallback = WareFeignClientImpl.class)
public interface WareFeignClient {

	@PostMapping("/ware/waresku/haStock")
	R haStock(@RequestBody List<Long> skuId);
}
