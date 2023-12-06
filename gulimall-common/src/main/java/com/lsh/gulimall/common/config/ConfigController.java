package com.lsh.gulimall.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO:
 *
 * @ClassName ConfigController
 * @Author codestar
 * @DATE 2021/6/3 上午12:52
 * @Version 1.0
 * @Description
 */
@RestController
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

	@Value("${useLocalCache:false}")
	private boolean useLocalCache;

	@RequestMapping("/get")
	public boolean get() {
		return useLocalCache;
	}

}
