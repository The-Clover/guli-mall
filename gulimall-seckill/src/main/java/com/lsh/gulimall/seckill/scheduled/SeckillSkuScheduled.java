package com.lsh.gulimall.seckill.scheduled;

import com.lsh.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * //TODO
 *
 * @Description: 秒杀商品定时上架 每天晚上3点 上架最近3天需要秒杀的商品
 * @Author: shihe
 * @Date: 2021-10-20 00:00
 */
@Service
//@EnableScheduling
//@Async
@Slf4j
public class SeckillSkuScheduled {

	@Autowired
	SeckillService seckillService;

	private static final String uploadLock = "seckill:upload:lock";

	@Autowired
	RedissonClient redissonClient;

	/**
	 * //TODO 幂等性处理
	 *
	 * @param
	 * @return: void
	 * @Description: 每天晚上3点上架
	 */
	@Scheduled(cron = "0 0/1 * * * ?")
	public void uploadSeckillSkuLatest3Days() {
		//1、重复上架无需处理
		// 分布式锁 只让一个服务执行
		RLock lock = redissonClient.getLock(uploadLock);
		lock.lock(10, TimeUnit.SECONDS);
		try {
			seckillService.uploadSeckillSkuLatest3Days();
		} finally {
			lock.unlock();
		}
	}

}
