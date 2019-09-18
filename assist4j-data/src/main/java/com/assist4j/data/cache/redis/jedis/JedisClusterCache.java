package com.assist4j.data.cache.redis.jedis;


import java.nio.charset.Charset;
import java.util.*;

import com.assist4j.data.cache.*;
import com.assist4j.data.cache.redis.RedisCache;
import com.assist4j.data.cache.serialize.DefaultSerialize;
import com.assist4j.data.cache.serialize.Serialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import redis.clients.jedis.JedisPubSub;


/**
 * @author yuwei
 */
public class JedisClusterCache implements RedisCache {
	private static final Logger log = LoggerFactory.getLogger(JedisClusterCache.class);
	private static final String UTF_8 = "utf-8";
	private BinaryJedisCluster jedisCluster;
	private Serialize serialize;


	public JedisClusterCache() {
		this(new DefaultSerialize());
	}
	public JedisClusterCache(Serialize serialize) {
		this.serialize = serialize;
	}


	public void setJedisCluster(BinaryJedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}

	public void setSerialize(Serialize serialize) {
		this.serialize = serialize;
	}


	@Override
	public <T>void publish(String channel, T value) {
		String v = serialize.encode(value);
		Charset charset = Charset.forName(UTF_8);
		jedisCluster.publish(channel.getBytes(charset), v.getBytes(charset));
	}

	@Override
	public <T>void subscribe(String channel, final MessageHandler<T> handler) {
		jedisCluster.subscribe(new JedisPubSub() {
			@Override
			public void onMessage(String channel, String message) {
				T t = serialize.decode(message);
				handler.handle(channel, t);
			}
		}, channel);
	}

	@Override
	public boolean contains(String key) {
		return jedisCluster.exists(key);
	}

	@Override
	public <T>boolean put(String key, T value, long expiredTime) {
		if (expiredTime <= 0) {
			throw new RuntimeException("Invalid expiredTime.");
		}

		String v = serialize.encode(value);
		Charset charset = Charset.forName(UTF_8);
		String res = jedisCluster.setex(key, (int) expiredTime, v.getBytes(charset));
		return "OK".equalsIgnoreCase(res);
	}

	@Override
	public <T>boolean put(String key, T value, Date expiredTime) {
		if (!expiredTime.after(new Date())) {
			throw new RuntimeException("Invalid expiredTime.");
		}

		return put(key, value, expiredTime.getTime() / 1000);
	}

	@Override
	public <T>T get(String key) {
		byte[] bytes = jedisCluster.getBytes(key);
		if (bytes == null) {
			return null;
		}

		Charset charset = Charset.forName(UTF_8);
		String str = new String(bytes, charset);
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
		jedisCluster.del(key);
	}

	@Override
	public <T> void hset(String key, String field, T value, long expiredTime) {
		if (expiredTime <= 0) {
			throw new RuntimeException("Invalid expiredTime.");
		}

		String v = serialize.encode(value);
		Charset charset = Charset.forName(UTF_8);
		jedisCluster.hset(key, field, v.getBytes(charset));
		jedisCluster.expire(key, (int) expiredTime);
	}

	@Override
	public Set<String> hfields(String key) {
		return jedisCluster.hkeys(key);
	}

	@Override
	public <T> T hget(String key, String field) {
		byte[] bytes = jedisCluster.hgetBytes(key, field);
		if (bytes == null) {
			return null;
		}

		Charset charset = Charset.forName(UTF_8);
		String str = new String(bytes, charset);
		try {
			return serialize.decode(str);
		} catch(Exception e) {
			log.error("数据异常！！！key: {}, field: {}, message: {}", key, field, e.getMessage());
			hdel(key, field);
			return null;
		}
	}

	@Override
	public void hdel(String key, String field) {
		jedisCluster.hdel(key, field);
	}

	private boolean setNx(String key, String owner, long expiredTime) {
		String v = serialize.encode(owner);
		String res = jedisCluster.set(key, v, "NX", "EX", (int) expiredTime);
		return "OK".equals(res);
	}
	private boolean setXx(String key, String owner, long expiredTime) {
		String v = serialize.encode(owner);
		String res = jedisCluster.set(key, v, "XX", "EX", (int) expiredTime);
		return "OK".equals(res);
	}

	private boolean reentrantLock(String key, String owner, long expiredTime) {
		String owner2 = this.get(key);
		if (owner.equals(owner2)) {
			if (setXx(key, owner, expiredTime)) {
				return true;
			}
		}
		return setNx(key, owner, expiredTime);
	}
	private boolean nonReentrantLock(String key, String owner, long expiredTime) {
		return setNx(key, owner, expiredTime);
	}

	@Override
	public boolean lock(String key, String owner, long expiredTime) {
		return lock(key, owner, expiredTime, false);
	}

	@Override
	public boolean lock(String key, String owner, long expiredTime, boolean reentrant) {
		if (reentrant) {
			return reentrantLock(key, owner, expiredTime);
		} else {
			return nonReentrantLock(key, owner, expiredTime);
		}
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
		Object result = jedisCluster.eval(redisScript.getScriptAsString(), Collections.singletonList(key), Collections.singletonList(v));
		return result != null && "1".equals(result.toString());
	}
}
