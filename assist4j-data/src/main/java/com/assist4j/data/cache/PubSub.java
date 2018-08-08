package com.assist4j.data.cache;



/**
 * @author yuwei
 */
public interface PubSub {
	/**
	 * 发布消息
	 * @param channel
	 * @param value
	 */
	<T>void publish(String channel, T value);

	/**
	 * 订阅消息
	 * @param channel
	 * @param handler
	 */
	<T>void subscribe(String channel, MessageHandler<T> handler);
}
