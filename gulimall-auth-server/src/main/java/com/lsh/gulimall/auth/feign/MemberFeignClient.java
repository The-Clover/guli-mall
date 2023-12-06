package com.lsh.gulimall.auth.feign;

import com.lsh.gulimall.auth.feign.impl.MemberFeignClientImpl;
import com.lsh.gulimall.auth.vo.SocialUser;
import com.lsh.gulimall.auth.vo.UserLoginVo;
import com.lsh.gulimall.auth.vo.UserRegisterVo;
import com.lsh.gulimall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Primary
@FeignClient(name = "gulimall-member")
public interface MemberFeignClient {

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/7/22 0:59
	 * @Description 注册
	 */
	@RequestMapping("/member/member/register")
	// member
	R register(@RequestBody UserRegisterVo registerVo);

	@RequestMapping("/member/member/login")
	R login(@RequestBody UserLoginVo userLoginVo);


	@PostMapping("/member/member/oauth2/login")
	R oauth2Login(@RequestBody SocialUser socialUser);
}
