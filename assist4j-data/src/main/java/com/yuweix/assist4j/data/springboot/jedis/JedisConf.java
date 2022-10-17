package com.yuweix.assist4j.data.springboot.jedis;


import com.yuweix.assist4j.core.json.Json;
import com.yuweix.assist4j.data.cache.redis.jedis.JedisCache;
import com.yuweix.assist4j.data.serializer.JsonSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;


/**
 * 单实例redis
 * @author yuwei
 */
public class JedisConf {
	@Bean(name = "jedisPoolConfig")
	public JedisPoolConfig jedisPoolConfig(@Value("${redis.pool.maxTotal:1024}") int maxTotal
			, @Value("${redis.pool.maxIdle:100}") int maxIdle
			, @Value("${redis.pool.minIdle:100}") int minIdle
			, @Value("${redis.pool.maxWaitMillis:10000}") long maxWaitMillis
			, @Value("${redis.pool.testOnBorrow:false}") boolean testOnBorrow) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);
		config.setMaxWaitMillis(maxWaitMillis);
		config.setTestOnBorrow(testOnBorrow);
		return config;
	}

	@Bean(name = "redisStandaloneConfiguration")
	public RedisStandaloneConfiguration redisStandaloneConfiguration(@Value("${redis.host:}") String host
			, @Value("${redis.port:0}") int port
			, @Value("${redis.dbIndex:0}") int dbIndex
			, @Value("${redis.needPassword:false}") boolean needPassword
			, @Value("${redis.password:}") String password) {
		RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
		conf.setHostName(host);
		if (port > 0) {
			conf.setPort(port);
		}
		if (dbIndex > 0) {
			conf.setDatabase(dbIndex);
		}
		if (needPassword) {
			conf.setPassword(RedisPassword.of(password));
		}
		return conf;
	}

	@Bean(name = "jedisConnectionFactory")
	public JedisConnectionFactory jedisConnectionFactory(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig
			, @Qualifier("redisStandaloneConfiguration") RedisStandaloneConfiguration config) {
		JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().usePooling().poolConfig(jedisPoolConfig).build();
		return new JedisConnectionFactory(config, jedisClientConfiguration);
	}

	@ConditionalOnMissingBean(name = "redisValueSerializer")
	@Bean(name = "redisValueSerializer")
	public RedisSerializer<Object> redisValueSerializer(Json json) {
		return new JsonSerializer(json);
	}

	@Bean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(@Qualifier("jedisConnectionFactory") RedisConnectionFactory connFactory
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
	public RedisMessageListenerContainer messageContainer(@Qualifier("jedisConnectionFactory") RedisConnectionFactory connFactory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connFactory);
		return container;
	}

	@ConditionalOnMissingBean(name = "redisCache")
	@Bean(name = "redisCache")
	public JedisCache redisCache(@Qualifier("redisTemplate") RedisTemplate<String, Object> template
			, RedisMessageListenerContainer messageContainer) {
		JedisCache cache = new JedisCache(template);
		cache.setMessageContainer(messageContainer);
		return cache;
	}
}
