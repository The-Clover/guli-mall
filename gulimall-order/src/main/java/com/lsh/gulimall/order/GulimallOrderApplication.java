package com.lsh.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * //TODO
 * <p>
 * RabbitMQ 的使用
 * 1. 引入amqp场景 RabbitAutoConfiguration就会自动生效
 * 2. 给容器自动配置了 RabbitTemplate AmqpAdmin CachingConnectionFactory RabbitMessagingTemplate
 * 3. @EnableRabbit : 开启Rabbit
 * 4. 给配置文件中配置 spring.rabbitmq.XXX信息
 *
 * @Author: codestar
 * @Date 9/1/21 9:14 PM
 * @Description: 订单模块
 **/
@EnableRabbit
@MapperScan("com.lsh.gulimall.order.dao")
@EnableDiscoveryClient
@SpringBootApplication
@EnableRedisHttpSession
@EnableFeignClients("com.lsh.gulimall.order.feign")
public class GulimallOrderApplication {
	public static void main(String[] args) {
		SpringApplication.run(GulimallOrderApplication.class, args);
	}
}