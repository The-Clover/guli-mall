package com.lsh.cart.vo;

import lombok.Data;

@Data
public class UserInfoTo {

	private Long userId;
	private String userKey;

	private boolean TempUser=false;

}
