package com.lsh.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.member.entity.MemberEntity;
import com.lsh.gulimall.member.entity.vo.MemberRegisterVo;
import com.lsh.gulimall.member.entity.vo.SocialUser;

import java.util.Map;

/**
 * 会员
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:34:03
 */
public interface MemberService extends IService<MemberEntity> {


	PageUtils queryPage(Map<String, Object> params);

	void register(MemberRegisterVo registerVo);


	void checkPhoneUnique(String phone);

	void checkUserNameUnique(String userName);

	MemberEntity login(SocialUser socialUser);
}

