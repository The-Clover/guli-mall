package com.lsh.gulimall.auth.vo;
/**
 * Copyright 2021 bejson.com
 */

import lombok.Data;

/**
 * Auto-generated: 2021-08-01 10:51:36
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class SocialUser {

	private String access_token;
	private int remind_in;
	private int expires_in;
	private String uid;
	private String isRealName;

}