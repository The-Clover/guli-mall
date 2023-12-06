package com.lsh.gulimall.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import javax.annotation.PostConstruct;


/**
 * //TODO
 *
 * @Author: codestar
 * @Date: 2021-09-09 23:57:51
 * @Description:
 */
@EnableFeignClients(basePackages = {"com.lsh.gulimall.auth.feign"})
@EnableDiscoveryClient
@SpringBootApplication
@EnableRedisHttpSession // 开启spring session
@Slf4j
public class GulimallAuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallAuthServerApplication.class, args);
	}

	@PostConstruct
	public void start() {
		log.warn("认证服务启动完成!");
	}
}
