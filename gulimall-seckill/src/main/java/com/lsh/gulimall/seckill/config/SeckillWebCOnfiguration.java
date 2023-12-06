package com.lsh.gulimall.seckill.config;

import com.lsh.gulimall.seckill.interceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SeckillWebCOnfiguration implements WebMvcConfigurer {

	@Autowired
	LoginUserInterceptor interceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		/*添加登陆拦截器*/
		registry.addInterceptor(interceptor).addPathPatterns("/**");
	}
}
