package com.lsh.gulimall.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class GulimallSessionConfig {


	/*自定义session作用域*/
	@Bean
	public CookieSerializer cookieSerializer() {
		DefaultCookieSerializer defaultCookieSerializer = new DefaultCookieSerializer();
		defaultCookieSerializer.setDomainName("gulimall.com");
		defaultCookieSerializer.setCookieName("GULISESSION");
		return defaultCookieSerializer;
	}

	/*自定义redis序列化器*/
	@Bean
	public RedisSerializer<Object> springSessionDefaultRedisSerializer() {

		return new GenericJackson2JsonRedisSerializer();
	}
}
