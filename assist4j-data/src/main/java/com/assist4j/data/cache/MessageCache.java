package com.assist4j.data.cache;



/**
 * @author yuwei
 */
public interface MessageCache {
	/**
	 * 发布消息
	 * @param channel
	 * @param value
	 */
	void publish(String channel, String value);

	/**
	 * 订阅消息
	 * @param channel
	 * @param handler
	 */
	void subscribe(String channel, MessageHandler handler);
}
