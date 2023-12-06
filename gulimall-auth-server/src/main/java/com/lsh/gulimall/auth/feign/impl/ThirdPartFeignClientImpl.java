package com.lsh.gulimall.auth.feign.impl;

import com.lsh.gulimall.auth.feign.ThirdPartFeignClient;
import com.lsh.gulimall.common.utils.R;
import org.springframework.stereotype.Component;

@Component
public class ThirdPartFeignClientImpl implements ThirdPartFeignClient {
	@Override
	public R sendCode(String phone, String code) {
		return R.error("调用验证码服务失败");
	}
}
