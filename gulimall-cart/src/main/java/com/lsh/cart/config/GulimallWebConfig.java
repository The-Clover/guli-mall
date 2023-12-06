package com.lsh.cart.config;

import com.lsh.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*定制化配置*/
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {


	/*添加拦截器 拦截所有请求*/
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
	}
}
