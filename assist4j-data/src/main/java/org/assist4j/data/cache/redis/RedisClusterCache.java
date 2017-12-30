package org.assist4j.data.cache.redis;


import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import org.assist4j.data.cache.Cache;
import org.assist4j.data.cache.CacheUtil;

import lombok.extern.slf4j.Slf4j;


/**
 * @author wei
 */
@Slf4j
public class RedisClusterCache implements Cache {
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
	public <T>void subscribe(String channel, JedisListener<T> jedisListener) {
		jedisCluster.subscribe(jedisListener, channel);
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
		if(expiredTime <= 0) {
			throw new RuntimeException("Invalid expiredTime.");
		}

		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, (int)expiredTime);
		return put(key, value, c.getTime());
	}

	@Override
	public <T>boolean put(String key, T value, Date expiredTime) {
		if(!expiredTime.after(new Date())) {
			throw new RuntimeException("Invalid expiredTime.");
		}

		boolean b = put0(key, value);
		if(!b) {
			return false;
		}
		jedisCluster.pexpireAt(key, expiredTime.getTime());
		return true;
	}

	@Override
	public <T>T get(String key) {
		byte[] bytes = jedisCluster.getBytes(key);
		if(bytes == null) {
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
}
