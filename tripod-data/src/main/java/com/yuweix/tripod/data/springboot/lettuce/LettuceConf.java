package com.yuweix.tripod.data.springboot.lettuce;


import com.yuweix.tripod.core.json.Json;
import com.yuweix.tripod.data.cache.redis.lettuce.LettuceCache;
import com.yuweix.tripod.data.serializer.JsonSerializer;
import com.yuweix.tripod.data.serializer.Serializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;


/**
 * 单实例redis
 * @author yuwei
 */
public class LettuceConf {
	@Bean(name = "lettuceClientConfiguration")
	public LettuceClientConfiguration clientConfiguration(@Value("${tripod.redis.pool.maxTotal:20}") int maxTotal
			, @Value("${tripod.redis.pool.maxIdle:10}") int maxIdle
			, @Value("${tripod.redis.pool.minIdle:10}") int minIdle
			, @Value("${tripod.redis.pool.maxWaitMillis:10000}") long maxWaitMillis
			, @Value("${tripod.redis.pool.timeBetweenEvictionRunsMillis:-1}") long timeBetweenEvictionRunsMillis
			, @Value("${tripod.redis.pool.testOnBorrow:false}") boolean testOnBorrow
			, @Value("${tripod.redis.timeoutMillis:5000}") long timeoutMillis) {
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxTotal(maxTotal);
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setMinIdle(minIdle);
		poolConfig.setMaxWaitMillis(maxWaitMillis);
		poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		poolConfig.setTestOnBorrow(testOnBorrow);
		LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
				.commandTimeout(Duration.ofMillis(timeoutMillis))
				.poolConfig(poolConfig)
				.build();
		return clientConfig;
	}

	@Bean(name = "redisStandaloneConfiguration")
	public RedisStandaloneConfiguration redisStandaloneConfiguration(@Value("${tripod.redis.host:}") String host
			, @Value("${tripod.redis.port:0}") int port
			, @Value("${tripod.redis.dbIndex:0}") int dbIndex
			, @Value("${tripod.redis.needPassword:false}") boolean needPassword
			, @Value("${tripod.redis.password:}") String password) {
		RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
		conf.setHostName(host);
		if (port > 0) {
			conf.setPort(port);
		}
		if (dbIndex >= 0) {
			conf.setDatabase(dbIndex);
		}
		if (needPassword) {
			conf.setPassword(RedisPassword.of(password));
		}
		return conf;
	}

	@Primary
	@ConditionalOnMissingBean(name = "lettuceConnectionFactory")
	@Bean(name = "lettuceConnectionFactory")
	public LettuceConnectionFactory lettuceConnectionFactory(@Qualifier("lettuceClientConfiguration") LettuceClientConfiguration clientConfig
			, @Qualifier("redisStandaloneConfiguration") RedisStandaloneConfiguration config) {
		LettuceConnectionFactory connFactory = new LettuceConnectionFactory(config, clientConfig);
		connFactory.setValidateConnection(false);
		connFactory.setShareNativeConnection(true);
		return connFactory;
	}

	@Bean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(@Qualifier("lettuceConnectionFactory") LettuceConnectionFactory connFactory) {
		RedisSerializer<?> redisSerializer = new StringRedisSerializer();
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connFactory);
		template.setKeySerializer(redisSerializer);
		template.setValueSerializer(redisSerializer);
		template.setEnableDefaultSerializer(true);
//		template.setEnableTransactionSupport(true);
		return template;
	}

	@ConditionalOnMissingBean(RedisMessageListenerContainer.class)
	@Bean
	public RedisMessageListenerContainer messageContainer(@Qualifier("lettuceConnectionFactory") LettuceConnectionFactory connFactory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connFactory);
		return container;
	}

	@ConditionalOnMissingBean(Serializer.class)
	@Bean
	public Serializer cacheSerializer(Json json) {
		return new JsonSerializer(json);
	}

	@ConditionalOnMissingBean(name = "redisCache")
	@Bean(name = "redisCache")
	public LettuceCache redisCache(@Qualifier("redisTemplate") RedisTemplate<String, Object> template
			, Serializer serializer
			, RedisMessageListenerContainer messageContainer) {
		LettuceCache cache = new LettuceCache(template, serializer);
		cache.setMessageContainer(messageContainer);
		return cache;
	}
}
