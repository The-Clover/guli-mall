package com.lsh.gulimall.order.web;

import com.lsh.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.UUID;

/**
 * //TODO
 *
 * @Author: codestar
 * @Date 9/5/21 3:58 AM
 * @Description:
 **/
@Controller
public class HelloController {

	@Autowired
	RabbitTemplate rabbitTemplate;

	@GetMapping("/{page}.html")
	public String listPage(@PathVariable("page") String page) {

		return page;
	}

	/**
	 * //TODO
	 *
	 * @param null
	 * @return: null
	 * @Description: 测试RabbitMQ监听过期订单
	 */
	@GetMapping("/test/createOrder")
	@ResponseBody
	public String createOrderTest() {
		OrderEntity orderEntity = new OrderEntity();
		orderEntity.setOrderSn(UUID.randomUUID().toString());
		orderEntity.setStatus(1);
		orderEntity.setModifyTime(new Date());

		rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", orderEntity);
		return "ok";
	}


}
