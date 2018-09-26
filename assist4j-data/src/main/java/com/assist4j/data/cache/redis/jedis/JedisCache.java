package com.assist4j.data.cache.redis.jedis;


import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.assist4j.data.cache.*;

import com.assist4j.data.cache.redis.RedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.serializer.RedisSerializer;


/**
 * @author yuwei
 */
public class JedisCache implements RedisCache {
	private static final Logger log = LoggerFactory.getLogger(JedisCache.class);
	private static final String CHARSET = "utf-8";
	private RedisTemplate<String, Object> redisTemplate;


	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}


	private static byte[] encode(final String str) {
		try {
			if (str == null) {
				throw new RuntimeException("value sent to redis cannot be null");
			}
			return str.getBytes(CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	private static String decode(final byte[] data) {
		try {
			return new String(data, CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public <T>void publish(final String channel, final T value) {
		final String v = CacheUtil.objectToString(value);
		redisTemplate.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.publish(encode(channel), encode(v));
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
							channel = decode(message.getChannel());
						}

						if (message.getBody() != null) {
							msg = decode(message.getBody());
						}

						T t = CacheUtil.stringToObject(msg);
						handler.handle(channel, t);
					}
				}, encode(channel));
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
			log.error("数据异常！！！key: {}, message: {}", key, e.getMessage());
			remove(key);
			return null;
		}
	}

	@Override
	public void remove(String key) {
		redisTemplate.delete(key);
	}

	@Override
	public <T> void hset(String key, String field, T value, long expiredTime) {
		if (expiredTime <= 0) {
			throw new RuntimeException("Invalid expiredTime.");
		}

		String v = CacheUtil.objectToString(value);
		redisTemplate.opsForHash().put(key, field, v);
		redisTemplate.expire(key, expiredTime, TimeUnit.SECONDS);
	}

	@Override
	public Set<String> hfields(String key) {
		Set<String> fieldSet = new HashSet<String>();
		Set<Object> objectSet = redisTemplate.opsForHash().keys(key);
		if (objectSet == null || objectSet.size() <= 0) {
			return fieldSet;
		}

		for (Object obj: objectSet) {
			fieldSet.add(obj.toString());
		}
		return fieldSet;
	}

	@Override
	public <T> T hget(String key, String field) {
		String str = (String) redisTemplate.opsForHash().get(key, field);
		if (str == null) {
			return null;
		}
		try {
			return CacheUtil.stringToObject(str);
		} catch (Exception e) {
			log.error("数据异常！！！key: {}, field: {}, message: {}", key, field, e.getMessage());
			hdel(key, field);
			return null;
		}
	}

	@Override
	public void hdel(String key, String field) {
		redisTemplate.opsForHash().delete(key, field);
	}

	@Override
	public boolean lock(final String key, final String owner, final long expiredTime) {
		Boolean b = redisTemplate.execute(new RedisCallback<Boolean>() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				RedisSerializer keySerializer = redisTemplate.getKeySerializer();
				byte[] bkey = keySerializer.serialize(key);

				RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
				byte[] bvalue = valueSerializer.serialize(owner);

				connection.setEx(bkey, expiredTime, bvalue);
				String val = (String) redisTemplate.opsForValue().get(key);
				return val != null && val.equals(owner);
			}
		});
		return b != null && b;
	}

	@Override
	public boolean unlock(String key, String owner) {
		String val = (String) redisTemplate.opsForValue().get(key);
		if (val == null || val.equals(owner)) {
			redisTemplate.delete(key);
			return true;
		} else {
			return false;
		}
	}
}
