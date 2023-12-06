package com.lsh.gulimall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsh.gulimall.common.utils.PageUtils;
import com.lsh.gulimall.common.utils.Query;
import com.lsh.gulimall.order.dao.OrderItemDao;
import com.lsh.gulimall.order.entity.MqMessageEntity;
import com.lsh.gulimall.order.entity.OrderEntity;
import com.lsh.gulimall.order.entity.OrderItemEntity;
import com.lsh.gulimall.order.service.OrderItemService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service("OrderItemService")
//@RabbitListener(queues = {"atguiguTest"})
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<OrderItemEntity> page = this.page(
				new Query<OrderItemEntity>().getPage(params),
				new QueryWrapper<OrderItemEntity>()
		);
		return new PageUtils(page);
	}

	@Override
	public String test() {

		return null;
	}

	/**
	 * @param message
	 * @return: void
	 * @Description: 参数可以写以下类型
	 * 1.Message 原生消息类型 body messageProperties
	 * 2.发送的消息的java类型  HashMap<String, Object>
	 * 3.Channel 当前传输数据的通道
	 * <p>
	 * 1.分布式情况下 同一个消息只能有一个客户端收到
	 * 2.只有一个消息完全处理完 才能接受下一个消息
	 * RabbitHandler 方法上 可以重载区分不同的消息
	 * RabbitListener 类与方法上
	 */
//	@RabbitListener(queues = {"atguiguTest"})
//	@RabbitHandler
//	public void recieveMessage(Message message, HashMap<String, Object> map, Channel channel) {
//		/*接受到消息.....(Body:'{"是否毕业":false,"姓名":"张三","年龄":18}' MessageProperties [headers={__ContentTypeId__=java.lang.Object, __KeyTypeId__=java.lang.Object, __TypeId__=java.util.HashMap}, contentType=application/json, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false, receivedExchange=hello.java.exchange, receivedRoutingKey=atguiguTest, deliveryTag=1, consumerTag=amq.ctag-TkbCdCfxqt2S0luR9QTfvw, consumerQueue=atguiguTest]) */
//		byte[] body = message.getBody(); //消息体
//		MessageProperties messageProperties = message.getMessageProperties(); //消息头
//
//
//		/*接受消息直接传入参数就行*/
//		log.info("接受到原生消息....." + message + "    类型:" + message.getClass());
//
////		接受到指定类型消息.....{是否毕业=false, 姓名=张三, 年龄=18}    类型:class java.util.HashMap
//		log.info("接受到指定类型消息....." + map + "    类型:" + map.getClass());
//	}




	/*3.消费端确认 默认自动确认 自动ack 消费端宕机会导致队列中的 未消费 消息丢失 */
	/*手动确认 保证消息不丢失*/
	/*
	 * 1.spring.rabbitmq.listener.simple.acknowledge-mode=manual 开启手动确认
	 *
	 * 2.Channel channel 在消费方法中加入参数 Channel (com.rabbitmq.client.Channel)
	 *
	 * 3.channel.basicAck(message.getMessageProperties().getDeliveryTag(),false); 手动确认
	 *
	 * 消息没有被消费 : unacked 即使消费端宕机 消息不会丢失 会变为ready状态 直到消费端手动确认 消息删除
	 *
	 * getDeliveryTag 消息标签 一个自增数字
	 * multiple 是否为批量模式
	 * basicAck 手动签收
	 * basicNack 显式拒绝
	 * 默认/业务处理失败 拒绝
	 * */
	@RabbitHandler
	public void recieveMessage2(Message message, MqMessageEntity mqMessageEntity, Channel channel) {
		try {
			log.info("签收消费消息");
			log.info("接受到消息:" + mqMessageEntity + "类型: " + mqMessageEntity.getClass());
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			/*channel中断*/
			e.printStackTrace();
		}
	}

	@RabbitHandler
	public void recieveMessage2(Message message, OrderEntity mqMessageEntity, Channel channel) {
		log.info("拒绝签收消息");
		try {
			/* 拒收 requeue 是否重新入队 true:重新放入队列 false:丢弃消息*/
			log.info("拒绝消息:" + mqMessageEntity + "类型: " + mqMessageEntity.getClass());
			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
		} catch (IOException e) {
			/*如果没有执行到 basicNack 默认消息不会被丢弃 消息变为ready状态  重新发送*/
//			try {
//				channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
//			} catch (IOException ex) {
//				ex.printStackTrace();
//			}
			e.printStackTrace();
		}

	}
}