package com.lsh.gulimall.order.listener;

import com.lsh.gulimall.order.entity.OrderEntity;
import com.lsh.gulimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RabbitListener(queues = {"order.release.queue"})
@Service
@Slf4j
public class OrderCloseListener {

	@Autowired
	OrderService orderService;

	@RabbitListener(queues = {"order.release.queue"})
	public void listener(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
		log.warn("收到过期订单信息 准备关闭订单{}", orderEntity);
		/*确认收到消息*/
		try {
			orderService.closeOrder(orderEntity);

//			// 支付宝手动调用收单
//			AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.app_id, AlipayConfig.merchant_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, AlipayConfig.sign_type);
//
//			//设置请求参数
//			AlipayTradeCloseRequest alipayRequest = new AlipayTradeCloseRequest();
//			//商户订单号，商户网站订单系统中唯一订单号
//			String out_trade_no = new String(request.getParameter("WIDTCout_trade_no").getBytes("ISO-8859-1"),"UTF-8");
//			//支付宝交易号
//			String trade_no = new String(request.getParameter("WIDTCtrade_no").getBytes("ISO-8859-1"),"UTF-8");
//			//请二选一设置
//
//			alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\"," +"\"trade_no\":\""+ trade_no +"\"}");
//
//			//请求
//			String result = alipayClient.execute(alipayRequest).getBody();
//
//			//输出
//			out.println(result);
//			System.out.println();


			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			e.printStackTrace();
			log.warn("关闭订单失败 ,等待下次关闭 !");
			// 重新回到消息队列
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
		}

	}
}
