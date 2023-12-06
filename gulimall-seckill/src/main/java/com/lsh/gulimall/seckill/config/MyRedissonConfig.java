package com.lsh.gulimall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRedissonConfig {

	@Value("${spring.redis.host}")
	private String ipAddr;

//	@Value("${spring.redis.password}")
//	private String password;

	// redission通过redissonClient对象使用 // 如果是多个redis集群，可以配置
	@Bean(destroyMethod = "shutdown")
	public RedissonClient redisson() {
		Config config = new Config();
		// 创建单例模式的配置
		config.useSingleServer().setAddress("redis://" + ipAddr + ":6379");
		/*集群模式*/
//		config.useClusterServers().addNodeAddress()
		return Redisson.create(config);
	}

}
