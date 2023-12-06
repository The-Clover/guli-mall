package com.lsh.gulimall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * //TODO
 *
 * @Author codestar
 * @Date 23:16 2021/6/1
 * @Description
 **/
@EnableTransactionManagement
@MapperScan(basePackages = "com.lsh.gulimall.ware.dao")
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients("com.lsh.gulimall.ware.feign")
@EnableRedisHttpSession
@EnableRabbit
public class GulimallWareApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallWareApplication.class, args);
	}

}
