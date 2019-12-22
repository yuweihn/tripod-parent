package com.assist4j.data.cache.redis;


import java.util.List;

import com.assist4j.data.cache.Cache;
import com.assist4j.data.cache.MessageHandler;


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

	<T>boolean lock(String key, T owner, long timeout);
	/**
	 * @param key
	 * @param owner
	 * @param timeout 单位：秒。
	 * @param reentrant   是否可重入
	 * @return
	 */
	<T>boolean lock(String key, T owner, long timeout, boolean reentrant);
	<T>boolean unlock(String key, T owner);

	String execute(String script, List<String> keyList, List<String> argList);
}
