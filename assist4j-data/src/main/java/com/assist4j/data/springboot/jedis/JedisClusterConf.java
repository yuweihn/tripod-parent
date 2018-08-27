package com.assist4j.data.springboot.jedis;


import com.assist4j.data.cache.Cache;
import com.assist4j.data.cache.redis.jedis.BinaryJedisCluster;
import com.assist4j.data.cache.redis.jedis.JedisClusterCache;
import com.assist4j.data.cache.redis.jedis.JedisClusterFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;


/**
 * redis集群
 * @author yuwei
 */
public class JedisClusterConf {

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

	@Bean(name = "jedisCluster", initMethod = "init")
	public JedisClusterFactory jedisClusterFactory(@Qualifier("jedisPoolConfig") JedisPoolConfig jedisPoolConfig
			, @Qualifier("redisNodeList") List<HostAndPort> redisNodeList
			, @Value("${redis.cluster.timeout:300000}") int timeout
			, @Value("${redis.cluster.maxRedirections:6}") int maxRedirections) {
		JedisClusterFactory factory = new JedisClusterFactory();
		factory.setJedisPoolConfig(jedisPoolConfig);
		factory.setRedisNodeList(redisNodeList);
		factory.setTimeout(timeout);
		factory.setMaxRedirections(maxRedirections);
		return factory;
	}

	@Bean(name = "redisCache")
	public Cache redisClusterCache(@Qualifier("jedisCluster") BinaryJedisCluster jedisCluster) {
		JedisClusterCache cache = new JedisClusterCache();
		cache.setJedisCluster(jedisCluster);
		return cache;
	}
}
