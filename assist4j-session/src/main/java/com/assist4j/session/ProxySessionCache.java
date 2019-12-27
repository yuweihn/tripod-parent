package com.assist4j.session;


import com.assist4j.session.cache.SessionCache;
import com.assist4j.session.conf.SessionConf;
import com.assist4j.session.conf.ValueSplit;


/**
 * @author yuwei
 */
public abstract class ProxySessionCache {
	public static boolean put(String key, String value) {
		SessionConf sessionConf = SessionConf.getInstance();
		SessionCache cache = sessionConf.getCache();

		long timeSec = sessionConf.getMaxInactiveInterval() * 60;
		ValueSplit valueSplit = sessionConf.getValueSplit();
		if (valueSplit == null || !valueSplit.getFlag()) {
			return cache.put(key, value, timeSec);
		} else {
			return SplitCacheUtil.put(key, value, timeSec, valueSplit.getMaxLength(), cache);
		}
	}

	public static String get(String key) {
		SessionConf sessionConf = SessionConf.getInstance();
		SessionCache cache = sessionConf.getCache();

		ValueSplit valueSplit = sessionConf.getValueSplit();
		if (valueSplit == null || !valueSplit.getFlag()) {
			return cache.get(key);
		} else {
			return SplitCacheUtil.get(key, cache);
		}
	}

	public static void remove(String key) {
		SessionConf sessionConf = SessionConf.getInstance();
		SessionCache cache = sessionConf.getCache();

		ValueSplit valueSplit = sessionConf.getValueSplit();
		if (valueSplit == null || !valueSplit.getFlag()) {
			cache.remove(key);
		} else {
			SplitCacheUtil.remove(key, cache);
		}
	}
}
