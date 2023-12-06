package com.lsh.gulimall.member.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lsh.gulimall.common.exception.BizCodeEnume;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.member.Feign.MemberFeignClient;
import com.lsh.gulimall.member.entity.MemberEntity;
import com.lsh.gulimall.member.entity.vo.MemberRegisterVo;
import com.lsh.gulimall.member.entity.vo.SocialUser;
import com.lsh.gulimall.member.exception.PhoneExistException;
import com.lsh.gulimall.member.exception.UserNameExistException;
import com.lsh.gulimall.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 会员
 *
 * @author codestar
 * @email codestar@gmail.com
 * @date 2021-06-01 00:34:03
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberFeignClient memberFeignClient;


	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/7/22 7:38
	 * @Description 登录
	 */
	@PostMapping("/oauth2/login")
	R oauth2Login(@RequestBody SocialUser socialUser) {
		MemberEntity login = memberService.login(socialUser);
		System.out.println(login);
		return R.ok().put("data", login);
	}

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/7/22 7:38
	 * @Description 登录
	 */
	@PostMapping("login")
	R login(@RequestBody MemberRegisterVo userLoginVo) {
		MemberEntity memberEntity = memberService.getOne(new QueryWrapper<MemberEntity>().eq("username", userLoginVo.getUserName()).or().eq("mobile", userLoginVo.getUserName()).or().eq("email", userLoginVo.getUserName()));
		if (memberEntity != null) {
			BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
			if (bCryptPasswordEncoder.matches(userLoginVo.getPassword(), memberEntity.getPassword())) {
				System.out.println("登录成功");
				return R.ok().put("data", memberEntity);
			}
		}
		System.out.println("登录信息有误");
		return R.error(BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getCode(), BizCodeEnume.LOGINACCT_PASSWORD_EXCEPTION.getMsg());
	}

	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/7/22 0:59
	 * @Description 注册
	 */
	@RequestMapping("/register") // member
	public R register(@RequestBody MemberRegisterVo registerVo) {
		try {
			memberService.register(registerVo);
			//异常机制：通过捕获对应的自定义异常判断出现何种错误并封装错误信息
		} catch (UserNameExistException userException) {//用户已存在
			return R.error(BizCodeEnume.USER_EXIST_EXCEPTION.getCode(), BizCodeEnume.USER_EXIST_EXCEPTION.getMsg());
		} catch (PhoneExistException phoneException) {// 手机已经注册
			return R.error(BizCodeEnume.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnume.PHONE_EXIST_EXCEPTION.getMsg());
		}
		return R.ok();
	}


	/**
	 * @return list
	 */
	@GetMapping("/")
	R getList() {
		List<MemberEntity> list = memberService.list();
		return R.ok().put("list", list).put("memberFeignClient", memberFeignClient.list());
	}

	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params) {
		PageUtils page = memberService.queryPage(params);

		return R.ok().put("page", page);
	}


	/**
	 * 信息
	 */
	@RequestMapping("/info/{id}")
	public R info(@PathVariable("id") Long id) {
		MemberEntity member = memberService.getById(id);

		return R.ok().put("member", member);
	}

	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody MemberEntity member) {
		memberService.save(member);

		return R.ok();
	}

	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody MemberEntity member) {
		memberService.updateById(member);

		return R.ok();
	}

	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] ids) {
		memberService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
