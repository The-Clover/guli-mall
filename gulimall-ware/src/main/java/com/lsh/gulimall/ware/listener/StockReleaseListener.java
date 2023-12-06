package com.lsh.gulimall.ware.listener;

import com.lsh.gulimall.common.to.mq.OrderTo;
import com.lsh.gulimall.common.to.mq.StockLockedTo;
import com.lsh.gulimall.ware.dao.WareSkuDao;
import com.lsh.gulimall.ware.service.WareOrderTaskDetailService;
import com.lsh.gulimall.ware.service.WareOrderTaskService;
import com.lsh.gulimall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = {"stock.release.stock.queue"})
@Slf4j
public class StockReleaseListener {


	@Autowired
	WareSkuService wareSkuService;

	@Autowired
	WareOrderTaskService wareOrderTaskService;

	@Autowired
	WareOrderTaskDetailService wareOrderTaskDetailService;

	@Autowired
	WareSkuDao wareSkuDao;

	/**
	 * //TODO
	 *
	 * @param detailTo
	 * @return: null
	 * @Description: RabbitMQ 延时队列自动解锁库存
	 * 解锁的条件
	 * 1、下订单成功，过期没有支付
	 * 2、手动取消订单
	 * 3、下订单成功，锁定库存成功，之后的其他业务失败了
	 */
	@RabbitHandler
	public void handleStockLockedRelease(StockLockedTo lockedTo, Message message, Channel channel) throws IOException {
		log.warn("下单超时 收到解锁库存的信息-->{}", lockedTo);
		/*解锁库存*/
		try {
			wareSkuService.unlockStockByOrder(lockedTo);
			/*解锁成功*/
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			/*消费失败 重新放入队列*/
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
		}

	}

	/**
	 * //TODO
	 *
	 * @param null
	 * @return: null
	 * @Description: 订单关闭 手动解锁库存
	 */
	@RabbitHandler
	public void handleOrderCloseRelease(OrderTo order, Message message, Channel channel) throws IOException {
		try {
			log.warn("订单已经关闭 收到解锁库存的信息-->{}", order);
			wareSkuService.unlockStockByOrder(order);
			/*手动确认消息*/
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			e.printStackTrace();
			/*解锁失败 重新放入消息队列 等待下次解锁*/
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
		}
	}
}
