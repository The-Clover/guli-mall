package com.lsh.gulimall.order.controller;

import com.lsh.gulimall.order.entity.MqMessageEntity;
import com.lsh.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RabbitController {


	@Autowired
	RabbitTemplate rabbitTemplate;

	@GetMapping("/sendMessage/{num}")
	String sendMessage(@PathVariable("num") Integer num) {
		for (int i = 1; i <= num; i++) {
			if (i % 2 == 0) {
				MqMessageEntity mqMessageEntity = new MqMessageEntity();
				mqMessageEntity.setMessageId(String.valueOf(i));
				rabbitTemplate.convertAndSend("hello.java.exchange","atguiguTest", mqMessageEntity,new CorrelationData(UUID.randomUUID().toString()));
			} else {

				OrderEntity OrderEntity = new OrderEntity();
				OrderEntity.setId(Long.parseLong(String.valueOf(i)));
				rabbitTemplate.convertAndSend("hello.java.exchange","atguiguTest", OrderEntity,new CorrelationData(UUID.randomUUID().toString()));
			}
		}
		return "消息发送成功!";
	}


}
