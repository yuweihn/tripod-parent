package com.yuweix.assist4j.data.springboot.lettuce;


import com.yuweix.assist4j.core.json.Json;
import com.yuweix.assist4j.data.cache.redis.lettuce.LettuceCache;
import com.yuweix.assist4j.data.serializer.JsonSerializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;


/**
 * redis集群
 * @author yuwei
 */
public class LettuceClusterConf {
	@Bean(name = "lettuceClientConfiguration")
	public LettuceClientConfiguration clientConfiguration(@Value("${redis.pool.maxTotal:1024}") int maxTotal
			, @Value("${redis.pool.maxIdle:100}") int maxIdle
			, @Value("${redis.pool.minIdle:100}") int minIdle
			, @Value("${redis.pool.maxWaitMillis:10000}") long maxWaitMillis
			, @Value("${redis.pool.testOnBorrow:false}") boolean testOnBorrow
			, @Value("${redis.timeoutMillis:5000}") long timeoutMillis) {
		GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		poolConfig.setMaxTotal(maxTotal);
		poolConfig.setMaxIdle(maxIdle);
		poolConfig.setMinIdle(minIdle);
		poolConfig.setMaxWaitMillis(maxWaitMillis);
		poolConfig.setTestOnBorrow(testOnBorrow);

		LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
				.commandTimeout(Duration.ofMillis(timeoutMillis))
				.poolConfig(poolConfig)
				.build();

		return clientConfig;
	}

	@Bean(name = "redisClusterConfiguration")
	public RedisClusterConfiguration redisClusterConfiguration(@Qualifier("redisNodeList") List<String> redisNodeList
			, @Value("${redis.cluster.timeout:300000}") int timeout
			, @Value("${redis.cluster.maxRedirections:6}") int maxRedirections) {
		RedisClusterConfiguration conf = new RedisClusterConfiguration(redisNodeList);
		conf.setMaxRedirects(maxRedirections);
		return conf;
	}

	@Bean(name = "lettuceConnectionFactory")
	public LettuceConnectionFactory lettuceConnectionFactory(@Qualifier("lettuceClientConfiguration") LettuceClientConfiguration clientConfig
			, @Qualifier("redisClusterConfiguration") RedisClusterConfiguration config) {
		LettuceConnectionFactory connFactory = new LettuceConnectionFactory(config, clientConfig);
		connFactory.setValidateConnection(true);
		connFactory.setShareNativeConnection(false);
		return connFactory;
	}

	@ConditionalOnMissingBean(name = "redisValueSerializer")
	@Bean(name = "redisValueSerializer")
	public RedisSerializer<Object> redisValueSerializer(Json json) {
		return new JsonSerializer(json);
	}

	@Bean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(@Qualifier("lettuceConnectionFactory") LettuceConnectionFactory connFactory
			, @Qualifier("redisValueSerializer") RedisSerializer<Object> redisValueSerializer) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(redisValueSerializer);
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

	@ConditionalOnMissingBean(name = "redisCache")
	@Bean(name = "redisCache")
	public LettuceCache redisCache(@Qualifier("redisTemplate") RedisTemplate<String, Object> template
			, RedisMessageListenerContainer messageContainer) {
		LettuceCache cache = new LettuceCache(template);
		cache.setMessageContainer(messageContainer);
		return cache;
	}
}
