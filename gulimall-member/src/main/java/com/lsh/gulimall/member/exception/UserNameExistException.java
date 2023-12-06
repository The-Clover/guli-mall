package com.lsh.gulimall.member.exception;

public class UserNameExistException extends RuntimeException {
	public UserNameExistException() {
		super("用户已注册");
	}
}
