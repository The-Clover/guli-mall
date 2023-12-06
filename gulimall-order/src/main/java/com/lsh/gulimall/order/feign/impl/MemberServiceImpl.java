package com.lsh.gulimall.order.feign.impl;

import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.order.feign.MemberService;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {
	@Override
	public R orderAddressInfo(Long memberId) {

		return R.error("查询订单确认页地址失败");
	}
}
