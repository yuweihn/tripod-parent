package com.assist4j.data.cache;




/**
 * @author yuwei
 */
public interface MessageHandler {
	void handle(String channel, String value);
}
