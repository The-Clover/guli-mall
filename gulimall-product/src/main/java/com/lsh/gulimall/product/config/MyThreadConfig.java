package com.lsh.gulimall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
@Configuration
public class MyThreadConfig {


	/**
	 * //TODO
	 *
	 * @param ThreadPoolConfigProperties pool !!! 从容器中拿
	 * @return
	 * @throws
	 * @date 2021/7/18 0:33
	 * @Description
	 */
	@Bean
	public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties pool) {
		return new ThreadPoolExecutor(pool.getCoreSize(), pool.getMaxSize(), pool.getKeepAliveTime(), TimeUnit.SECONDS, new LinkedBlockingQueue<>(100000), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
	}

}
