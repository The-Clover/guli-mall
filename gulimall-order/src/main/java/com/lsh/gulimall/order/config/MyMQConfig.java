package com.lsh.gulimall.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * //TODO
 *
 * @Description: 延时队列配置 订单分布式事务
 * @Author: shihe
 * @Date: 2021-10-10 23:25
 */
@Configuration
public class MyMQConfig {

	/*容器中的Binding Queue Exchange 都会创建 所以只需要吧相关组件放入容器即可 如果RabbitMQ中已存在相关组件 相关属性不会覆盖 也不会发生变化*/

	/**
	 * //TODO
	 *
	 * @param
	 * @return: Queue
	 * @Description: 死信队列
	 * 队列名字 是否持久化 是否排他 是否自动删除 自定义参数
	 * public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
	 */
	@Bean
	public Queue orderDelayQueue() {
		Map<String, Object> map = new HashMap<>();
		/*死信队列配置*/
		map.put("x-dead-letter-exchange", "order-event-exchange");
		map.put("x-dead-letter-routing-key", "order.release.order");
		/*死信过期时间*/
		map.put("x-message-ttl", 60000);
		return new Queue("order.delay.queue", true, false, false, map);
	}

	@Bean
	public Queue orderReleaseOrderQueue() {

		return new Queue("order.release.queue", true, false, false);
	}

	/**
	 * //TODO
	 *
	 * @param null
	 * @return: null
	 * @Description: String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
	 */
	@Bean
	public Exchange orderEventExchange() {
		return new TopicExchange("order-event-exchange", true, false);
	}

	/**
	 * //TODO
	 *
	 * @param null
	 * @return: null
	 * @Description: 绑定关系
	 * String destination, DestinationType destinationType, String exchange, String routingKey, Map<String, Object> arguments
	 * 目的地 目的地类型 交换机 路由键 自定义参数
	 */
	@Bean
	public Binding orderCreateOrderBingding() {
		return new Binding("order.delay.queue", Binding.DestinationType.QUEUE, "order-event-exchange", "order.create.order", null);
	}

	@Bean
	public Binding orderReleaseOrderBingding() {
		return new Binding("order.release.queue", Binding.DestinationType.QUEUE, "order-event-exchange", "order.release.order", null);
	}

	/**
	 * //TODO
	 *
	 * @param null
	 * @return: null
	 * @Description: 订单释放交换机
	 */
	@Bean
	public Binding orderReleaseOtherBingding() {
		return new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE, "order-event-exchange", "order.release.other.#", null);
	}

	@Bean
	public Queue orderSeckillOrderQueue(){
		return  new Queue("order.seckill.order.queue", true, false, false);
	}


	@Bean
	public Binding orderSeckillOrderBingding() {
		return new Binding("order.seckill.order.queue", Binding.DestinationType.QUEUE, "order-event-exchange", "order.seckill.order", null);
	}



//	@RabbitListener(queues = {"order.release.queue"})
//	public void listener(OrderEntity orderEntity, Channel channel, Message message) {
//		System.out.println("收到过期订单信息 准备关闭订单");
//		System.out.println(orderEntity);
//
//		/*确认收到消息*/
//		try {
//			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}


}
