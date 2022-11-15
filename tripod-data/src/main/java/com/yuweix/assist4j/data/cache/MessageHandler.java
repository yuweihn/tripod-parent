package com.yuweix.tripod.data.cache;




/**
 * @author yuwei
 */
public interface MessageHandler {
	void handle(String channel, String message);
}
