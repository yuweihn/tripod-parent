package com.assist4j.data.cache;



/**
 * @author yuwei
 */
public interface MessageHandler<T> {
	void handle(String channel, T value);
}
