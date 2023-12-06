package com.lsh.gulimall.order.listener;

import com.lsh.gulimall.common.to.mq.SeckillOrderTo;
import com.lsh.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RabbitListener(queues = "order.seckill.order.queue")
public class OrderSeckillListener {

	@Autowired
	OrderService orderService;

	@RabbitHandler
	public void listener(SeckillOrderTo SeckillOrderTo, Channel channel, Message message) throws IOException {
		log.warn("秒杀单信息{}", SeckillOrderTo);
		/*确认收到消息*/
		try {
			// 创建秒杀订单
			orderService.createSeckillOrder(SeckillOrderTo);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			e.printStackTrace();
			log.warn("关闭订单失败 ,等待下次关闭 !");
			// 重新回到消息队列
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
		}
	}


}
