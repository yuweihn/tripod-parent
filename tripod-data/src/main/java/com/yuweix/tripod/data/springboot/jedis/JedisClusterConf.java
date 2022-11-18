package com.yuweix.tripod.data.springboot.jedis;


import com.yuweix.tripod.core.json.Json;
import com.yuweix.tripod.data.cache.redis.jedis.JedisClusterCache;
import com.yuweix.tripod.data.cache.redis.jedis.JedisClusterFactory;
import com.yuweix.tripod.data.serializer.JsonSerializer;
import com.yuweix.tripod.data.serializer.Serializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;


/**
 * redis集群
 * @author yuwei
 */
public class JedisClusterConf {

	@Bean(name = "jedisPoolConfig")
	public JedisPoolConfig jedisPoolConfig(@Value("${tripod.redis.pool.maxTotal:20}") int maxTotal
			, @Value("${tripod.redis.pool.maxIdle:10}") int maxIdle
			, @Value("${tripod.redis.pool.minIdle:10}") int minIdle
			, @Value("${tripod.redis.pool.maxWaitMillis:10000}") long maxWaitMillis
			, @Value("${tripod.redis.pool.testOnBorrow:false}") boolean testOnBorrow) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMinIdle(minIdle);
		config.setMaxWaitMillis(maxWaitMillis);
		config.setTestOnBorrow(testOnBorrow);
		return config;
	}

	@Bean(name = "jedisCluster", initMethod = "init")
	public JedisClusterFactory jedisClusterFactory(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig
			, @Qualifier("redisNodeList") List<HostAndPort> redisNodeList
			, @Value("${tripod.redis.cluster.timeout:300000}") int timeout
			, @Value("${tripod.redis.cluster.maxRedirections:6}") int maxRedirections) {
		JedisClusterFactory factory = new JedisClusterFactory();
		factory.setJedisPoolConfig(jedisPoolConfig);
		factory.setRedisNodeList(redisNodeList);
		factory.setTimeout(timeout);
		factory.setMaxRedirections(maxRedirections);
		return factory;
	}

	@ConditionalOnMissingBean(Serializer.class)
	@Bean
	public Serializer cacheSerializer(Json json) {
		return new JsonSerializer(json);
	}

	@ConditionalOnMissingBean(name = "redisCache")
	@Bean(name = "redisCache")
	public JedisClusterCache redisClusterCache(@Qualifier("jedisCluster") JedisCluster jedisCluster
			, Serializer serializer) {
		JedisClusterCache cache = new JedisClusterCache(serializer);
		cache.setJedisCluster(jedisCluster);
		return cache;
	}
}
