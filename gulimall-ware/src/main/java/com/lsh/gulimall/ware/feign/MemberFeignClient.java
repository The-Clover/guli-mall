package com.lsh.gulimall.ware.feign;

import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.ware.feign.impl.ProductFeignClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Primary
@FeignClient(name = "gulimall-member")
public interface MemberFeignClient {
	/**
	 * 收获地址信息
	 */
	@RequestMapping("member/memberreceiveaddress/info/{id}")
	// @RequiresPermissions("member:memberreceiveaddress:info")
	R info(@PathVariable("id") Long id);
}
