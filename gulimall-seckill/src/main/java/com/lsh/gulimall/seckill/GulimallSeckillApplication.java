package com.lsh.gulimall.seckill;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableDiscoveryClient
@EnableFeignClients("com.lsh.gulimall.seckill.feign")
@EnableRedisHttpSession
@EnableRabbit
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GulimallSeckillApplication {
	public static void main(String[] args) {
		SpringApplication.run(GulimallSeckillApplication.class, args);
	}
}
