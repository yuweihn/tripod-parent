package com.assist4j.data.cache.redis;


import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import com.assist4j.data.cache.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPubSub;


/**
 * @author yuwei
 */
public class RedisClusterCache implements Cache, MessageCache, DistLock {
	private static final Logger log = LoggerFactory.getLogger(RedisClusterCache.class);
	private static final String UTF_8 = "utf-8";
	private BinaryJedisCluster jedisCluster;


	public void setJedisCluster(BinaryJedisCluster jedisCluster) {
		this.jedisCluster = jedisCluster;
	}


	@Override
	public <T>void publish(String channel, T value) {
		String v = CacheUtil.objectToString(value);
		Charset charset = Charset.forName(UTF_8);
		jedisCluster.publish(channel.getBytes(charset), v.getBytes(charset));
	}

	@Override
	public <T>void subscribe(String channel, final MessageHandler<T> handler) {
		jedisCluster.subscribe(new JedisPubSub() {
			@Override
			public void onMessage(String channel, String message) {
				T t = CacheUtil.stringToObject(message);
				handler.handle(channel, t);
			}
		}, channel);
	}

	@Override
	public boolean contains(String key) {
		return jedisCluster.exists(key);
	}

	private <T>boolean put0(String key, T value) {
		String v = CacheUtil.objectToString(value);
		Charset charset = Charset.forName(UTF_8);
		jedisCluster.set(key, v.getBytes(charset));
		return true;
	}

	@Override
	public <T>boolean put(String key, T value, long expiredTime) {
		if (expiredTime <= 0) {
			throw new RuntimeException("Invalid expiredTime.");
		}

		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, (int) expiredTime);
		return put(key, value, c.getTime());
	}

	@Override
	public <T>boolean put(String key, T value, Date expiredTime) {
		if (!expiredTime.after(new Date())) {
			throw new RuntimeException("Invalid expiredTime.");
		}

		boolean b = put0(key, value);
		if (!b) {
			return false;
		}
		jedisCluster.pexpireAt(key, expiredTime.getTime());
		return true;
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
			return CacheUtil.stringToObject(str);
		} catch(Exception e) {
			log.error("数据异常！！！key={}", key);
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

		String v = CacheUtil.objectToString(value);
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
			return CacheUtil.stringToObject(str);
		} catch(Exception e) {
			log.error("数据异常！！！key={}, field={}", key, field);
			hdel(key, field);
			return null;
		}
	}

	@Override
	public void hdel(String key, String field) {
		jedisCluster.hdel(key, field);
	}

	@Override
    public boolean lock(String key, String owner, long expiredTime) {
        String res = jedisCluster.setex(key, (int) expiredTime, owner);
        return "1".equals(res);
    }

    @Override
    public boolean unlock(String key, String owner) {
        String val = get(key);
        if (val == null || val.equals(owner)) {
            Long reply = jedisCluster.del(key);
            return reply != null && reply > 0;
        } else {
            return false;
        }
    }
}
