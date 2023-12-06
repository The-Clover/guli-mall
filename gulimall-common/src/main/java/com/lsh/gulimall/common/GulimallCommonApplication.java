package com.lsh.gulimall.common;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author codestar
 */
/*exclude = {DataSourceAutoConfiguration.class}*/
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, MybatisPlusAutoConfiguration.class, RedisAutoConfiguration.class})
@EnableDiscoveryClient
public class GulimallCommonApplication {
	public static void main(String[] args) {
		SpringApplication.run(GulimallCommonApplication.class, args);
	}
}