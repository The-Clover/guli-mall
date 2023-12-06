package com.lsh.gulimall.product.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * //TODO
 *
 * @Author shihe
 * @Date 23:40 2021/7/7
 * @Description
 **/
@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class MyCacheConfig {

	/*拿到 properties/yml 配置文件*/
	@Autowired
	CacheProperties cacheProperties;

	@Bean
	RedisCacheConfiguration getRedisCacheConfiguration() {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
//		redisCacheConfiguration=redisCacheConfiguration.entryTtl();
		/*key 序列化机制*/

		config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
		/*value 序列化机制 改为json GenericJackson2JsonRedisSerializer() 任意对象转json*/
		config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));


		/*拿到配置文件的值*/
		CacheProperties.Redis redisProperties = cacheProperties.getRedis();
		if (redisProperties.getTimeToLive() != null) {
			config = config.entryTtl(redisProperties.getTimeToLive());
		}
		if (redisProperties.getKeyPrefix() != null) {
			config = config.prefixKeysWith(redisProperties.getKeyPrefix());
		}
		if (!redisProperties.isCacheNullValues()) {
			config = config.disableCachingNullValues();
		}
		if (!redisProperties.isUseKeyPrefix()) {
			config = config.disableKeyPrefix();
		}
		return config;
	}

}
