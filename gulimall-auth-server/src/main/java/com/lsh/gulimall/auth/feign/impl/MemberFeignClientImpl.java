package com.lsh.gulimall.auth.feign.impl;

import com.lsh.gulimall.auth.feign.MemberFeignClient;
import com.lsh.gulimall.auth.vo.SocialUser;
import com.lsh.gulimall.auth.vo.UserLoginVo;
import com.lsh.gulimall.auth.vo.UserRegisterVo;
import com.lsh.gulimall.common.utils.R;
import org.springframework.stereotype.Component;

@Component
public class MemberFeignClientImpl implements MemberFeignClient {
	@Override
	public R register(UserRegisterVo registerVo) {
		return R.error("注册服务调用失败");
	}

	@Override
	public R login(UserLoginVo userLoginVo) {
		return R.error("登录服务调用失败");
	}

	@Override
	public R oauth2Login(SocialUser socialUser) {
		return R.error("登录服务调用失败");
	}
}
