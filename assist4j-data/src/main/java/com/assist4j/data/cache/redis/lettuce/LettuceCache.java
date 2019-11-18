package com.assist4j.data.cache.redis.lettuce;


import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.assist4j.data.cache.*;
import com.assist4j.data.cache.redis.RedisCache;
import com.assist4j.data.cache.serialize.DefaultSerialize;
import com.assist4j.data.cache.serialize.Serialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;


/**
 * @author yuwei
 */
public class LettuceCache implements RedisCache {
	private static final Logger log = LoggerFactory.getLogger(LettuceCache.class);
	private static final String CHARSET = "utf-8";
	private Serialize serialize;
	protected RedisTemplate<String, Object> redisTemplate;


	public LettuceCache() {
		this(new DefaultSerialize());
	}
	public LettuceCache(Serialize serialize) {
		this.serialize = serialize;
	}


	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void setSerialize(Serialize serialize) {
		this.serialize = serialize;
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
		final String v = serialize.encode(value);
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

						T t = serialize.decode(msg);
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

	@Override
	public <T>boolean put(String key, T value, long timeout) {
		if (timeout <= 0) {
			throw new RuntimeException("Invalid parameter[timeout].");
		}

		String v = serialize.encode(value);
		redisTemplate.opsForValue().set(key, v, timeout, TimeUnit.SECONDS);
		return true;
	}

	@Override
	public <T>T get(String key) {
		String str = (String) redisTemplate.opsForValue().get(key);
		if (str == null) {
			return null;
		}
		try {
			return serialize.decode(str);
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

	private boolean setNx(String key, String owner, long expiredTime) {
		String v = serialize.encode(owner);
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockNx.lua")));
		String result = redisTemplate.execute(redisScript, Collections.singletonList(key), v, "" + expiredTime);
		return result != null && "OK".equalsIgnoreCase(result);
	}
	@SuppressWarnings("unused")
	private boolean setXx(String key, String owner, long expiredTime) {
		String v = serialize.encode(owner);
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockXx.lua")));
		String result = redisTemplate.execute(redisScript, Collections.singletonList(key), v, "" + expiredTime);
		return result != null && "OK".equalsIgnoreCase(result);
	}
	private boolean setXxEquals(String key, String owner, long expiredTime) {
		String v = serialize.encode(owner);
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockXxEquals.lua")));
		String result = redisTemplate.execute(redisScript, Collections.singletonList(key), v, "" + expiredTime);
		return result != null && "OK".equalsIgnoreCase(result);
	}

	@Override
	public boolean lock(String key, String owner, long expiredTime) {
		return lock(key, owner, expiredTime, false);
	}

	@Override
	public boolean lock(String key, String owner, long expiredTime, boolean reentrant) {
		return reentrant && setXxEquals(key, owner, expiredTime) || setNx(key, owner, expiredTime);
	}

	@Override
	public boolean unlock(String key, String owner) {
		if (!contains(key)) {
			return true;
		}
		String v = serialize.encode(owner);
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<Long>();
		redisScript.setResultType(Long.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/releaseLock.lua")));
		Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), v);
		return result != null && "1".equals(result.toString());
	}

	@Override
	public String execute(String script, List<String> keyList, List<String> argList) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptText(script);
		return redisTemplate.execute(redisScript, keyList, argList == null ? new Object[0] : argList.toArray());
	}
}
