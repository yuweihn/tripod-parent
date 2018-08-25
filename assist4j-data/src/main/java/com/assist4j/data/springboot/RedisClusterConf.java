package com.assist4j.data.springboot;


import com.assist4j.data.cache.redis.RedisCache;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.List;


/**
 * redis集群
 * @author yuwei
 */
public class RedisClusterConf {
	@Bean(name = "lettuceClientConfiguration")
	public LettuceClientConfiguration clientConfiguration(@Value("${redis.socket.connectTimeoutMillis:10000}") long connectTimeoutMillis) {
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
		return connFactory;
	}

	@Bean(name = "redisTemplate")
	public RedisTemplate<String, Object> redisTemplate(@Qualifier("lettuceConnectionFactory") LettuceConnectionFactory connFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(connFactory);
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template;
	}

	@Bean(name = "redisCache")
	public RedisCache redisCache(@Qualifier("redisTemplate") RedisTemplate<String, Object> template) {
		RedisCache cache = new RedisCache();
		cache.setRedisTemplate(template);
		return cache;
	}
}
