package com.assist4j.data.springboot;


import com.assist4j.data.cache.redis.RedisCache;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;


/**
 * 一主多从redis
 * @author yuwei
 */
public class RedisMsConf {
	@Bean(name = "lettuceClientConfiguration")
	public LettuceClientConfiguration clientConfiguration(@Value("${redis.socket.connectTimeoutMillis:5000}") long connectTimeoutMillis) {
		SocketOptions socketOptions = SocketOptions.builder()
				.keepAlive(true)
				.tcpNoDelay(true)
				.connectTimeout(Duration.ofMillis(connectTimeoutMillis))
				.build();

		ClientOptions clientOptions = ClientOptions.builder()
				.socketOptions(socketOptions)
				.pingBeforeActivateConnection(true)
				.autoReconnect(false)
				.build();

		LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettuceClientConfiguration.builder();
		builder.clientOptions(clientOptions);
		return builder.build();
	}

	@Bean(name = "redisSentinelConfiguration")
	public RedisSentinelConfiguration redisSentinelConfiguration(@Value("${redis.master.name}") String masterName
			, @Value("${redis.sentinel.ip}") String host
			, @Value("${redis.sentinel.port}") int port
			, @Value("${redis.dbIndex:0}") int dbIndex
			, @Value("${redis.needPassword:false}") boolean needPassword
			, @Value("${redis.password:}") String password) {
		RedisSentinelConfiguration conf = new RedisSentinelConfiguration();
		RedisNode redisNode = new RedisNode.RedisNodeBuilder().withName(masterName).build();
		conf.setMaster(redisNode);
		conf.setDatabase(dbIndex);
		if (needPassword) {
			conf.setPassword(RedisPassword.of(password));
		}

		Set<RedisNode> sentinels = new HashSet<RedisNode>();
		sentinels.add(new RedisNode(host, port));
		conf.setSentinels(sentinels);
		return conf;
	}

	@Bean(name = "lettuceConnectionFactory")
	public LettuceConnectionFactory lettuceConnectionFactory(@Qualifier("lettuceClientConfiguration") LettuceClientConfiguration clientConfig
			, @Qualifier("redisSentinelConfiguration") RedisSentinelConfiguration config) {
		LettuceConnectionFactory connFactory = new LettuceConnectionFactory(config, clientConfig);
		connFactory.setValidateConnection(true);
		return connFactory;
	}

	@Bean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(@Qualifier("lettuceConnectionFactory") LettuceConnectionFactory connFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(connFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		template.setEnableDefaultSerializer(true);
		template.setEnableTransactionSupport(true);
		return template;
	}

	@Bean(name = "redisCache")
	public RedisCache redisCache(@Qualifier("redisTemplate") RedisTemplate<String, Object> template) {
		RedisCache cache = new RedisCache();
		cache.setRedisTemplate(template);
		return cache;
	}
}
