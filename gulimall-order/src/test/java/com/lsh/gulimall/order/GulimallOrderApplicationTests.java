package com.lsh.gulimall.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GulimallOrderApplicationTests {

//	/*测试RabbitMQ*/
//
//	/**
//	 * @return: null
//	 * @Description: 1.如何创建Exchange Queue Binding
//	 * 2.如何收发消息
//	 */
//
//	@Autowired
//	AmqpAdmin amqpAdmin;
//
//	@Autowired
//	RabbitMessagingTemplate rabbitMessagingTemplate;
//
//	@Autowired
//	RabbitTemplate rabbitTemplate;
//
//	@Test
//	public void createExchange() {
//		/*声明一个交换机 持久化 不自动删除 没有删除*/
//		amqpAdmin.declareExchange(new DirectExchange("hello.java.exchange", true, false, null));
//		log.info("交换机创建成功");
//
//		/*创建队列 返回队列名字*/
//		String atguiguTest = amqpAdmin.declareQueue(new Queue("atguiguTest", true, false, false, null));
//		log.info("队列创建成功 atguiguTest = " + atguiguTest);
//
//
//		/*绑定 参数:  目的地(可以是交换或者队列名 )  目的地类型 交换机 路由键 参数*/
//		amqpAdmin.declareBinding(new Binding("atguiguTest", Binding.DestinationType.QUEUE, "hello.java.exchange", "atguiguTest", null));
//		log.info("绑定成功");
//
//
//
//
//		/*给交换机发送消息*/
////		RabbitTemplate rabbitTemplate = rabbitMessagingTemplate.getRabbitTemplate();
//		/*原声发送消息*/
////		Message message = new Message("哈哈哈哈哈哈哈哈哈哈哈哈哈哈".getBytes(StandardCharsets.UTF_8), new MessageProperties());
////		rabbitTemplate.send("atguiguTest", message);
//
//		Map<String, Object> map = new HashMap<>();
//		map.put("姓名", "张三");
//		map.put("年龄", 18);
//		map.put("是否毕业", false);
//		/*rO0ABXNyABFqYXZhLnV0aWwuSGFzaE1hcAUH2sHDFmDRAwACRgAKbG9hZEZhY3RvckkACXRocmVzaG9sZHhwP0AAAAAAAAx3CAAAABAAAAADdAAM5piv5ZCm
//5q+V5Liac3IAEWphdmEubGFuZy5Cb29sZWFuzSBygNWc+u4CAAFaAAV2YWx1ZXhwAHQABuWnk+WQjXQABuW8oOS4iXQABuW5tOm+hHNyABFqYXZhLmxhbmcu
//SW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAASeA==*/
//		/*发送的消息是个对象 那么会使用序列化机制 所以对象必须实现 Serializable 序列化接口*/
////		rabbitTemplate.convertAndSend("hello.java.exchange", "atguiguTest", map);
//
//
//
//		/*建议 更改序列化机制 Jackson2JsonMessageConverter 后 content_type:	application/json */
//		/*{"是否毕业":false,"姓名":"张三","年龄":18}*/
//		rabbitTemplate.convertAndSend("hello.java.exchange", "atguiguTest", map);
//
//		/*{"是否毕业":false,"姓名":"张三","年龄":18}*/
////		rabbitTemplate.convertAndSend("hello.java.exchange", "atguiguTest", JSON.toJSONString(map));
//		log.info("消息发送成功");
//
//	}
//
//
//	/**
//	 * @return: null
//	 * @Description: 接受消息 注解@RabbitListener(queues = {"atguiguTest"})
//	 */
//	@RabbitListener(queues = {"atguiguTest"})
//	@Test
//	public void getMessage() {
//	}

}
