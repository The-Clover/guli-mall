package com.lsh.gulimall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author codestar
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, RedisAutoConfiguration.class})
@EnableDiscoveryClient
@EnableFeignClients
public class GulimallGatewayApplication {
	public static void main(String[] args) {
		for (String arg : args) {
			System.out.println(arg);
		}
		SpringApplication.run(GulimallGatewayApplication.class, args);
	}

}