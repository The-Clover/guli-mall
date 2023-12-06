package com.lsh.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author codestar
 */
@EnableTransactionManagement
@MapperScan(basePackages = "com.lsh.gulimall.product.dao")
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "com.lsh.gulimall.product.feign")
@EnableRedisHttpSession
public class GulimallProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallProductApplication.class, args);
	}

}
