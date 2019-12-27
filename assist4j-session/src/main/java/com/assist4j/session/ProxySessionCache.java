package com.assist4j.session;


import com.assist4j.session.cache.SessionCache;
import com.assist4j.session.conf.SessionConf;


/**
 * @author yuwei
 */
public abstract class ProxySessionCache {
	public static boolean put(String key, String value) {
		SessionConf sessionConf = SessionConf.getInstance();
		SessionCache cache = sessionConf.getCache();

		long timeSec = sessionConf.getMaxInactiveInterval() * 60;
		return cache.put(key, value, timeSec);
	}

	public static String get(String key) {
		SessionConf sessionConf = SessionConf.getInstance();
		SessionCache cache = sessionConf.getCache();

		return cache.get(key);
	}

	public static void remove(String key) {
		SessionConf sessionConf = SessionConf.getInstance();
		SessionCache cache = sessionConf.getCache();

		cache.remove(key);
	}
}
