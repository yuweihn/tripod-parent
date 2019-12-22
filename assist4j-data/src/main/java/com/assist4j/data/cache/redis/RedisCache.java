package com.assist4j.data.cache.redis;


import com.assist4j.data.cache.Cache;
import com.assist4j.data.cache.MessageHandler;

import java.util.List;


/**
 * @author yuwei
 */
public interface RedisCache extends Cache {
	/**
	 * 发布消息
	 * @param channel
	 * @param message
	 */
	void publish(String channel, String message);

	/**
	 * 订阅消息
	 * @param channel
	 * @param handler
	 */
	void subscribe(String channel, MessageHandler handler);

	String execute(String script, List<String> keyList, List<String> argList);
}
