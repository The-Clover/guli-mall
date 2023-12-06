package com.lsh.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * //TODO
 *
 * @Author: codestar
 * @Date 9/1/21 12:01 AM
 * @Description: 购物车模块
 **/
@EnableDiscoveryClient
@EnableFeignClients("com.lsh.cart.feign")
@SpringBootApplication
@EnableRedisHttpSession
public class GulimallCartApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallCartApplication.class, args);
	}

}