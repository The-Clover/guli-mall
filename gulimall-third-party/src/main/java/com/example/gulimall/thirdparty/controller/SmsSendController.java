package com.example.gulimall.thirdparty.controller;

import com.example.gulimall.thirdparty.component.SmsComponent;
import com.lsh.gulimall.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/third-party/sms")
public class SmsSendController {

	@Autowired
	SmsComponent smsComponent;


	/**
	 * //TODO
	 *
	 * @param
	 * @return
	 * @throws
	 * @date 2021/7/19 1:30
	 * @Description 提供给别的服务调用
	 */
	@PostMapping("/sendCode")
	R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {

		return smsComponent.sendSmsCode(phone, code) ? R.ok() : R.error("验证码发送失败");
	}
}