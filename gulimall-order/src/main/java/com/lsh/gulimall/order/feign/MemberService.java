package com.lsh.gulimall.order.feign;

import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.order.feign.impl.MemberServiceImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "gulimall-member")
@Primary
public interface MemberService {
	/**
	 * 订单确认也所需的地址信息
	 */
	@RequestMapping("member/memberreceiveaddress/orderAddressInfo/{memberId}")
	// @RequiresPermissions("member:memberreceiveaddress:list")
	public R orderAddressInfo(@PathVariable("memberId") Long memberId);

}
