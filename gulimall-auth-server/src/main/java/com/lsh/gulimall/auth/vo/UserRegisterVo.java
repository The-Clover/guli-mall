package com.lsh.gulimall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * //TODO
 *
 * @Author shihe
 * @Date 23:36 2021/7/21
 * @Description 注册vo
 **/
@Data
public class UserRegisterVo {

	@NotEmpty(message = "用户名不能为空")
	@Length(min = 6, max = 18, message = "用户名必须为6-18位字符")
	private String userName;
	@NotEmpty(message = "密码不能为空")
	@Length(min = 6, max = 18, message = "密码必须为6-18位字符")
	private String password;

	@NotEmpty(message = "手机号不能为空")
	@Pattern(regexp = "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[3,5,6,7,8])" + "|(18[0-9])|(19[8,9]))\\d{8}$", message = "手机号格式不正确")
	private String phone;
	@NotEmpty(message = "验证码格式不能为空")
	@Length(min = 6, max = 6, message = "验证码格式不正确")
	private String code;

}
