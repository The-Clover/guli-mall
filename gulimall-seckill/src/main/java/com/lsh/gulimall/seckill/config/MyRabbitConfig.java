package com.lsh.gulimall.seckill.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Slf4j
public class MyRabbitConfig {

	private RabbitTemplate rabbitTemplate;

	@Primary
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(getMessageConverter());
		this.rabbitTemplate = rabbitTemplate;
		setConfirmCallback();
		return rabbitTemplate;
	}

	/**
	 * @return: MessageConverter
	 * @Description: 自定义Rabbit的MessageConverter
	 */
	@Bean
	public MessageConverter getMessageConverter() {
		/*容器中存在自定义的MessageConverter 就用容器中的 没有就用SimpleMessageConverter */
		return new Jackson2JsonMessageConverter();
	}

	/**
	 * @param
	 * @return: void
	 * @Description: PostConstruct MyRabbitConfig 构造器创建完对象后调用方法
	 * <p>
	 */
	public void setConfirmCallback() {
		/*1.设置确认回调 消息发送者->交换机*/
		/*
		 * 1.spring.rabbitmq.publisher-confirms=true
		 * 2.设置rabbitTemplate的ConfirmCallback(默认回调)
		 * */
		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
			/*回调*/

			/**
			 * @param correlationData   当前消息的唯一关联数据 消息唯一id
			 * @param ack   消息发送者->交换机 消息是否成功收到
			 * @param cause 失败原因
			 * @return: void
			 * @Description:
			 */
			@Override
			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
				/*
				    correlationData = null
					ack = true  抵达Broker代理 rabbit服务器确认收到
					cause = null
				* */

				System.out.println("correlationData = " + correlationData);
				System.out.println("ack = " + ack);
				System.out.println("cause = " + cause);
			}
		});

		/*2.设置投递回调 交换机->队列*/
		/*
		 *  开启发送端消息抵达队列的确认
		 *  1.spring.rabbitmq.publisher-returns=true
		 *      spring.rabbitmq.template.mandatory=true
		 *  2.设置消息抵达队列的rabbitTemplate- ReturnCallback
		 * */
		rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
			/**
			 * 只要交换机没有将消息投递给指定的队列 就触发此回调
			 * @param message 投递失败的消息详细信息
			 * @param replyCode 状态吗
			 * @param replyText 回复文本
			 * @param exchange 交换机
			 * @param routingKey  路由键
			 * @return: void
			 * @Description:
			 */
			@Override
			public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
				System.out.println("消息投递失败");
				System.out.println("message = " + message);
				System.out.println("replyCode = " + replyCode);
				System.out.println("replyText = " + replyText);
				System.out.println("exchange = " + exchange);
				System.out.println("routingKey = " + routingKey);

				// 修改数据库消息错误状态

			}
		});

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

	}

}
