package com.assist4j.data.cache.redis.jedis;


import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.assist4j.data.cache.MessageHandler;
import com.assist4j.data.cache.redis.RedisCache;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import redis.clients.jedis.JedisPubSub;


/**
 * @author yuwei
 */
public class JedisClusterCache implements RedisCache {
	private static final String UTF_8 = "utf-8";
	protected BinaryJedisCluster jedisCluster;


	public JedisClusterCache() {
		
	}
	
	
	public void setJedisCluster(BinaryJedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}
	
	
	@Override
	public void publish(String channel, String value) {
		Charset charset = Charset.forName(UTF_8);
		jedisCluster.publish(channel.getBytes(charset), value.getBytes(charset));
	}

	@Override
	public void subscribe(String channel, final MessageHandler handler) {
		jedisCluster.subscribe(new JedisPubSub() {
			@Override
			public void onMessage(String channel, String message) {
				handler.handle(channel, message);
			}
		}, channel);
	}

	@Override
	public boolean contains(String key) {
		return jedisCluster.exists(key);
	}

	@Override
	public boolean put(String key, String value, long timeout) {
		if (timeout <= 0) {
			throw new RuntimeException("Invalid parameter[timeout].");
		}
		
		String res = jedisCluster.setex(key, (int) timeout, value.getBytes(Charset.forName(UTF_8)));
		return "OK".equalsIgnoreCase(res);
	}

	@Override
	public <T> boolean put(String key, T value, long timeout) {
		return put(key, JSON.toJSONString(value), timeout);
	}

	@Override
	public String get(String key) {
		byte[] bytes = jedisCluster.getBytes(key);
		if (bytes == null) {
			return null;
		}
		
		return new String(bytes, Charset.forName(UTF_8));
	}

	@Override
	public <T> T get(String key, Class<T> clz) {
		String val = get(key);
		if (val == null) {
			return null;
		}
		return JSON.parseObject(val, clz);
	}

	@Override
	public void remove(String key) {
		jedisCluster.del(key);
	}

	private boolean setNx(String key, String owner, long timeout) {
		String res = jedisCluster.set(key, owner, "NX", "EX", (int) timeout);
		return "OK".equalsIgnoreCase(res);
	}
	@SuppressWarnings("unused")
	private boolean setXx(String key, String owner, long timeout) {
		String res = jedisCluster.set(key, owner, "XX", "EX", (int) timeout);
		return "OK".equalsIgnoreCase(res);
	}
	private boolean setXxEquals(String key, String owner, long timeout) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/getLockXxEquals.lua")));
		Object result = jedisCluster.eval(redisScript.getScriptAsString(), Collections.singletonList(key), Arrays.asList(owner, "" + timeout));
		return result != null && "OK".equalsIgnoreCase(result.toString());
	}

	@Override
	public boolean lock(String key, String owner, long timeout) {
		return lock(key, owner, timeout, false);
	}

	@Override
	public boolean lock(String key, String owner, long timeout, boolean reentrant) {
		return reentrant && setXxEquals(key, owner, timeout) || setNx(key, owner, timeout);
	}

	@Override
	public boolean unlock(String key, String owner) {
		if (!contains(key)) {
			return true;
		}
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<Long>();
		redisScript.setResultType(Long.class);
		redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/releaseLock.lua")));
		Object result = jedisCluster.eval(redisScript.getScriptAsString(), Collections.singletonList(key), Collections.singletonList(owner));
		return result != null && "1".equals(result.toString());
	}

	@Override
	public String execute(String script, List<String> keyList, List<String> argList) {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<String>();
		redisScript.setResultType(String.class);
		redisScript.setScriptText(script);
		Object result = jedisCluster.eval(redisScript.getScriptAsString(), keyList, argList == null ? new ArrayList<String>() : argList);
		return result == null ? null : result.toString();
	}
}
