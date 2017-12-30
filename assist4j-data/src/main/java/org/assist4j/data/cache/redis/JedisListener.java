package org.assist4j.data.cache.redis;


import org.assist4j.data.cache.CacheUtil;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPubSub;
import redis.clients.util.SafeEncoder;


/**
 * @author yuwei
 */
@Slf4j
public abstract class JedisListener<T> extends JedisPubSub implements MessageListener {
	@Override
	public void onMessage(Message message, byte[] pattern) {
		String channel = null;
		String msg = null;

		if(message.getChannel() != null) {
			channel = SafeEncoder.encode(message.getChannel());
		}

		if(message.getBody() != null) {
			msg = SafeEncoder.encode(message.getBody());
		}

		onMessage(channel, msg);
	}

	@Override
	public void onMessage(String channel, String message) {
		try {
			T t = CacheUtil.stringToObject(message);
			listen(channel, t);
		} catch(Exception e) {
			log.error("", e);
		}
	}

	public abstract void listen(String channel, T value);
}
