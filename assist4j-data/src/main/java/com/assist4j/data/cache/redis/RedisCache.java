package com.assist4j.data.cache.redis;


import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.assist4j.data.cache.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import redis.clients.util.SafeEncoder;


/**
 * @author yuwei
 */
public class RedisCache implements Cache, MessageCache, DistLock {
	private static final Logger log = LoggerFactory.getLogger(RedisCache.class);
	private RedisTemplate<String, Object> redisTemplate;


	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}


	@Override
	public <T>void publish(final String channel, final T value) {
		final String v = CacheUtil.objectToString(value);
		redisTemplate.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.publish(SafeEncoder.encode(channel), SafeEncoder.encode(v));
				return null;
			}
		});
	}

	@Override
	public <T>void subscribe(final String channel, final MessageHandler<T> handler) {
		redisTemplate.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.subscribe(new MessageListener() {
					@Override
					public void onMessage(Message message, byte[] bytes) {
						String channel = null;
						String msg = null;

						if (message.getChannel() != null) {
							channel = SafeEncoder.encode(message.getChannel());
						}

						if (message.getBody() != null) {
							msg = SafeEncoder.encode(message.getBody());
						}

						T t = CacheUtil.stringToObject(msg);
						handler.handle(channel, t);
					}
				}, SafeEncoder.encode(channel));
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
		if (expiredTime <= 0) {
			throw new RuntimeException("Invalid expiredTime.");
		}

		boolean b = put0(key, value);
		b = b && redisTemplate.expire(key, expiredTime, TimeUnit.SECONDS);
		return b;
	}

	@Override
	public <T>boolean put(String key, T value, Date expiredTime) {
		if (!expiredTime.after(new Date())) {
			throw new RuntimeException("Invalid expiredTime.");
		}

		boolean b = put0(key, value);
		b = b && redisTemplate.expireAt(key, expiredTime);
		return b;
	}

	@Override
	public <T>T get(String key) {
		String str = (String) redisTemplate.opsForValue().get(key);
		if (str == null) {
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

	@Override
	public boolean lock(String key, String owner, long expiredTime) {
		Boolean b = redisTemplate.execute(new RedisCallback<Boolean>() {
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				return connection.setEx(SafeEncoder.encode(key), expiredTime, SafeEncoder.encode(owner));
			}
		});
		return b != null && b;
	}

	@Override
	public boolean unlock(String key, String owner) {
		String val = (String) redisTemplate.opsForValue().get(key);
		if (val == null || val.equals(owner)) {
			Boolean b = redisTemplate.delete(key);
			return b != null && b;
		} else {
			return false;
		}
	}
}
