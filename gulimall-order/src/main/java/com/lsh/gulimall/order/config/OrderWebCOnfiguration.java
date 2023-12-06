package com.lsh.gulimall.order.config;

import com.lsh.gulimall.order.interceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class OrderWebCOnfiguration implements WebMvcConfigurer {

	@Autowired
	LoginUserInterceptor interceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		/*添加登陆拦截器*/
		registry.addInterceptor(interceptor).addPathPatterns("/**");
	}
}
