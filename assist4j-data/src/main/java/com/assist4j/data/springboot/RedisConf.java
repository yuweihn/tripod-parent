package com.assist4j.data.springboot;


import com.assist4j.data.cache.Cache;
import com.assist4j.data.cache.redis.JedisPoolConnFactory;
import com.assist4j.data.cache.redis.RedisCache;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;


/**
 * 单实例redis
 * @author yuwei
 */
public class RedisConf {

	@Bean(name = "jedisPoolConfig")
	public JedisPoolConfig jedisPoolConfig(@Value("${redis.pool.maxTotal:1024}") int maxTotal
			, @Value("${redis.pool.maxIdle:200}") int maxIdle
			, @Value("${redis.pool.maxWaitMillis:10000}") long maxWaitMillis
			, @Value("${redis.pool.testOnBorrow:false}") boolean testOnBorrow) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMaxWaitMillis(maxWaitMillis);
		config.setTestOnBorrow(testOnBorrow);
		return config;
	}

	@Bean(name = "jedisConnectionFactory")
	public JedisPoolConnFactory jedisPoolConnFactory(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig
			, @Value("${redis.host:}") String host
			, @Value("${redis.port:0}") int port
			, @Value("${redis.dbIndex:0}") int dbIndex
			, @Value("${redis.needPassword:false}") boolean needPassword
			, @Value("${redis.password:}") String password) {
		JedisPoolConnFactory factory = new JedisPoolConnFactory();
		factory.setPoolConfig(jedisPoolConfig);
		factory.setHostName(host);
		factory.setPort(port);
		factory.setDatabase(dbIndex);
		factory.setNeedPassword(needPassword);
		factory.setPassword(password);
		return factory;
	}

	@Bean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(@Qualifier("jedisConnectionFactory") RedisConnectionFactory connFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(connFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template;
	}

	@Bean(name = "redisCache")
	public Cache redisCache(@Qualifier("redisTemplate") RedisTemplate<String, Object> template) {
		RedisCache cache = new RedisCache();
		cache.setRedisTemplate(template);
		return cache;
	}
}
