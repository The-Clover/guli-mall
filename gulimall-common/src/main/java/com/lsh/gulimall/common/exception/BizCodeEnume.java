package com.lsh.gulimall.common.exception;

/**
 * @author codestar
 */

public enum BizCodeEnume {
	VALID_EXCEPTION(10001, "参数格式校验失败"),
	TOO_MANY_REQUEST(10002, "请求流量过大"),
	PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),
	UNKNOW_EXCEPTION(10000, "未知异常"),
	SMS_CODE_EXCEPTION(10002, "短信验证码频率过高"),
	USER_EXIST_EXCEPTION(15001, "用户已存在"),
	LOGINACCT_PASSWORD_EXCEPTION(15001, "登录信息有误"),
	NO_STOCK_EXCEPTION(21000, "商品库存不足"),
	PHONE_EXIST_EXCEPTION(15002, "手机号已存在");

	private Integer code;
	private String msg;


	BizCodeEnume(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}
