package org.assist4j.data.cache.redis;


import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.assist4j.data.cache.Cache;
import org.assist4j.data.cache.CacheUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import lombok.extern.slf4j.Slf4j;
import redis.clients.util.SafeEncoder;


/**
 * @author yuwei
 */
@Slf4j
public class RedisCache implements Cache {
	private RedisTemplate<String, Object> redisTemplate;


	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}


	@Override
	public <T>void publish(final String channel, final T value) {
		String v = CacheUtil.objectToString(value);
		redisTemplate.execute(new RedisCallback<Object>(){
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.publish(SafeEncoder.encode(channel), SafeEncoder.encode(v));
				return null;
			}
		});
	}

	@Override
	public <T>void subscribe(final String channel, final JedisListener<T> jedisListener) {
		redisTemplate.execute(new RedisCallback<Object>(){
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.subscribe(jedisListener, SafeEncoder.encode(channel));
				return null;
			}
		});
	}

	@Override
	public boolean contains(String key) {
		Object object = redisTemplate.opsForValue().get(key);
		return object != null;
	}

	private <T>boolean put0(String key, T value) {
		String v = CacheUtil.objectToString(value);
		redisTemplate.opsForValue().set(key, v);
		return true;
	}

	@Override
	public <T>boolean put(String key, T value, long expiredTime) {
		if(expiredTime <= 0) {
			throw new RuntimeException("Invalid expiredTime.");
		}

		boolean b = put0(key, value);
		b = b && redisTemplate.expire(key, expiredTime, TimeUnit.SECONDS);
		return b;
	}

	@Override
	public <T>boolean put(String key, T value, Date expiredTime) {
		if(!expiredTime.after(new Date())) {
			throw new RuntimeException("Invalid expiredTime.");
		}

		boolean b = put0(key, value);
		b = b && redisTemplate.expireAt(key, expiredTime);
		return b;
	}

	@Override
	public <T>T get(String key) {
		String str = (String)redisTemplate.opsForValue().get(key);
		if(str == null) {
			return null;
		}
		try {
			return CacheUtil.stringToObject(str);
		} catch(Exception e) {
			log.error("数据异常！！！key={}", key);
			remove(key);
			return null;
		}
	}

	@Override
	public void remove(String key) {
		redisTemplate.delete(key);
	}

}
