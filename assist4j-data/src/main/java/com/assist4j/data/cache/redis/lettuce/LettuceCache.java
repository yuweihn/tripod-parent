package com.assist4j.data.cache.redis.lettuce;


import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.assist4j.data.cache.MessageHandler;
import com.assist4j.data.cache.redis.RedisCache;
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
	protected RedisTemplate<String, Object> redisTemplate;


	public LettuceCache() {
		
	}


	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}


	@Override
	public void publish(final String channel, final String message) {
		redisTemplate.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.publish(channel.getBytes(StandardCharsets.UTF_8), message.getBytes(StandardCharsets.UTF_8));
				return null;
			}
		});
	}

	@Override
	public void subscribe(final String channel, final MessageHandler handler) {
		redisTemplate.execute(new RedisCallback<Object>() {
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				connection.subscribe(new MessageListener() {
					@Override
					public void onMessage(Message message, byte[] bytes) {
						String channel = null;
						String msg = null;
						
						if (message.getChannel() != null) {
							channel = new String(message.getChannel(), StandardCharsets.UTF_8);
						}
						
						if (message.getBody() != null) {
							msg = new String(message.getBody(), StandardCharsets.UTF_8);
						}
						
						handler.handle(channel, msg);
					}
				}, channel.getBytes(StandardCharsets.UTF_8));
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
	public <T> boolean put(String key, T value, long timeout) {
		if (timeout <= 0) {
			throw new RuntimeException("Invalid parameter[timeout].");
		}

		if (value.getClass() == String.class) {
			redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
		} else {
			redisTemplate.opsForValue().set(key, JSON.toJSONString(value), timeout, TimeUnit.SECONDS);
		}
		return true;
	}

	@Override
	public String get(String key) {
		return (String) redisTemplate.opsForValue().get(key);
	}

	@Override
	public <T> T get(String key, Class<T> clz) {
		String val = get(key);
		if (val == null) {
			return null;
		}
		return clz == String.class ? (T) val : JSON.parseObject(val, clz);
	}

	@Override
	public <T> T get(String key, TypeReference<T> type) {
		String val = get(key);
		if (val == null) {
			return null;
		}
		return type.getType() == String.class ? (T) val : JSON.parseObject(val, type);
	}

	@Override
	public void remove(String key) {
		redisTemplate.delete(key);
	}

	private boolean setNx(String key, String owner, long timeout) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockNx.lua")));
		String result = redisTemplate.execute(redisScript, Collections.singletonList(key), owner, "" + timeout);
		return result != null && "OK".equalsIgnoreCase(result);
	}
	@SuppressWarnings("unused")
	private boolean setXx(String key, String owner, long timeout) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockXx.lua")));
		String result = redisTemplate.execute(redisScript, Collections.singletonList(key), owner, "" + timeout);
		return result != null && "OK".equalsIgnoreCase(result);
	}
	private boolean setXxEquals(String key, String owner, long timeout) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockXxEquals.lua")));
		String result = redisTemplate.execute(redisScript, Collections.singletonList(key), owner, "" + timeout);
		return result != null && "OK".equalsIgnoreCase(result);
	}

	@Override
	public <T>boolean lock(String key, T owner, long timeout) {
		return lock(key, owner, timeout, false);
	}

	@Override
	public <T>boolean lock(String key, T owner, long timeout, boolean reentrant) {
		String val = null;
		if (owner.getClass() == String.class) {
			val = (String) owner;
		} else {
			val = JSON.toJSONString(owner);
		}
		return reentrant && setXxEquals(key, val, timeout) || setNx(key, val, timeout);
	}

	@Override
	public <T>boolean unlock(String key, T owner) {
		if (!contains(key)) {
			return true;
		}
		String val = null;
		if (owner.getClass() == String.class) {
			val = (String) owner;
		} else {
			val = JSON.toJSONString(owner);
		}
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<Long>();
		redisScript.setResultType(Long.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/releaseLock.lua")));
		Long result = redisTemplate.execute(redisScript, Collections.singletonList(key), val);
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
