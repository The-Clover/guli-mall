package com.lsh.gulimall.auth.controller;


import com.alibaba.fastjson.TypeReference;
import com.lsh.gulimall.auth.feign.MemberFeignClient;
import com.lsh.gulimall.auth.feign.ThirdPartFeignClient;
import com.lsh.gulimall.auth.vo.UserLoginVo;
import com.lsh.gulimall.auth.vo.UserRegisterVo;
import com.lsh.gulimall.common.constant.AuthServerConstant;
import com.lsh.gulimall.common.constant.CartConstant;
import com.lsh.gulimall.common.exception.BizCodeEnume;
import com.lsh.gulimall.common.utils.R;
import com.lsh.gulimall.common.vo.MemberRespVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@Slf4j
public class LoginController {


	@Autowired
	ThirdPartFeignClient thirdPartFeignClient;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Autowired
	MemberFeignClient memberFeignClient;

	/**
	 * 发送一个请求直接跳转到页面
	 * <p>
	 * spring mvc viewController: 将请求和页面映射 不用写页面跳转方法 --> GulimallWebConfig
	 * 发送验证码
	 *
	 * @return
	 */
	@GetMapping("/sms/sendCode")
	@ResponseBody
	R sendCode(@RequestParam("phone") String phone) {

		// ^ 匹配输入字符串开始的位置
		// \d 匹配一个或多个数字，其中 \ 要转义，所以是 \\d
		// $ 匹配输入字符串结尾的位置
		String regExp = "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[3,5,6,7,8])" + "|(18[0-9])|(19[8,9]))\\d{8}$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(phone);
		if (!m.matches()) {
			return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), "手机号格式错误!");
		}


		String oldCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);

		if (!StringUtils.isEmpty(oldCode)) {
			/*60s秒内不能重复发送验证码*/
			if (System.currentTimeMillis() - Long.parseLong(oldCode.split("_")[1]) < 60000) {
				return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
			}
		}

		String code = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
		/*5分钟过期*/
		stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, code + "_" + System.currentTimeMillis(), 5, TimeUnit.MINUTES);
		R r = thirdPartFeignClient.sendCode(phone, code);


		System.out.println(r.getCode() == 0 ? "发送成功" : "发送失败");
		System.out.println(r);
		return R.ok();
	}


	/**
	 * //TODO 分布式session问题
	 * 重定向携带数据 RedirectAttributes 数据放入session中 只要从session中取出自动销毁
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/7/21 23:35
	 * @Description 注册
	 */
	@PostMapping("/register")
	public String register(@Valid UserRegisterVo registerVo,  // 注册信息
						   BindingResult result,
						   RedirectAttributes attributes) {
		//1.判断校验是否通过
		Map<String, String> errors = new HashMap<>();
		if (result.hasErrors()) {
			//1.1 如果校验不通过，则封装校验结果
			result.getFieldErrors().forEach(item -> {
				// 获取错误的属性名和错误信息
				errors.put(item.getField(), item.getDefaultMessage());
				//1.2 将错误信息封装到session中
				attributes.addFlashAttribute("errors", errors);
			});
			//1.2 重定向到注册页
			return "redirect:http://auth.gulimall.com/reg.html";
		} else {//2.若JSR303校验通过

			//判断验证码是否正确
			String code = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + registerVo.getPhone());
			//2.1 如果对应手机的验证码不为空且与提交的相等-》验证码正确
			if (!StringUtils.isEmpty(code) && registerVo.getCode().equalsIgnoreCase(code.split("_")[0])) {
				//2.1.1 使得验证后的验证码失效
				stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + registerVo.getPhone());

				//2.1.2 远程调用会员服务注册
				R r = memberFeignClient.register(registerVo);
				if (r.getCode() == 0) {
					//调用成功，重定向登录页
					return "redirect:http://auth.gulimall.com/login.html";
				} else {
					//调用失败，返回注册页并显示错误信息
					String msg = (String) r.get("msg");
					errors.put("msg", msg);
					attributes.addFlashAttribute("errors", errors);
					return "redirect:http://auth.gulimall.com/reg.html";
				}
			} else {
				//2.2 验证码错误
				errors.put("code", "验证码错误");
				attributes.addFlashAttribute("errors", errors);
				return "redirect:http://auth.gulimall.com/reg.html";
			}
		}
	}


	@PostMapping("/login")
	String login(UserLoginVo userLoginVo, RedirectAttributes redirectAttributes, HttpSession httpSession, HttpServletResponse response) {
		R login = memberFeignClient.login(userLoginVo);
		if (login.getCode() == 0) {
			MemberRespVo data = login.getData("data", new TypeReference<MemberRespVo>() {
			});
			httpSession.setAttribute(AuthServerConstant.LOGIN_USER, data);
			if (httpSession.getAttribute("url") != null) {
				System.out.println("redirect:" + httpSession.getAttribute("url"));
				Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, data.getId().toString());
				cookie.setDomain("gulimall.com");
				cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
				response.addCookie(cookie);
				return "redirect:" + httpSession.getAttribute("url");
			}
			return "redirect:http://gulimall.com";
		}
		System.out.println("登录失败: " + login.getData("msg", new TypeReference<String>() {
		}));
		/*错误消息*/
		HashMap<String, String> stringStringHashMap = new HashMap<>();
		stringStringHashMap.put("msg", login.getData("msg", new TypeReference<String>() {
		}));

		redirectAttributes.addFlashAttribute("errors", stringStringHashMap);
		return "redirect:http://auth.gulimall.com/login.html";
	}


	@GetMapping("/login.html")
	public String login(HttpSession httpSession, HttpServletRequest request) {
		Object attribute = httpSession.getAttribute(AuthServerConstant.LOGIN_USER);
		String url = request.getParameter("url");
		if (url != null) {
			httpSession.setAttribute("url", url);
		}
		if (attribute != null) {
			return "redirect:http://gulimall.com";
		}

		return "login";
	}

	/**
	 * @param null
	 * @return: null
	 * @Description: 退出登陆
	 */
	@GetMapping("logout")
	public String logout(HttpSession httpSession) {
		Object attribute = httpSession.getAttribute(AuthServerConstant.LOGIN_USER);
		if (attribute != null) {
			httpSession.removeAttribute(AuthServerConstant.LOGIN_USER);
		}
		return "redirect:http://gulimall.com";
	}

}
