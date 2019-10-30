package com.assist4j.session;


import com.assist4j.session.cache.SessionCache;
import com.assist4j.session.conf.SessionConf;
import com.assist4j.session.conf.ValueSplit;

import java.util.ArrayList;
import java.util.List;


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
			int oldSize = parseValueSize(key);

			List<String> valList = split(value, valueSplit.getMaxLength());
			int newSize = valList.size();
			boolean b = cache.put(key, "" + newSize, timeSec);
			for (int i = 0; i < newSize; i++) {
				b &= cache.put(key + "." + i, valList.get(i), timeSec + 60);
			}

			for (int i = newSize; i < oldSize; i++) {
				cache.remove(key + "." + i);
			}

			return b;
		}
	}

	public static String get(String key) {
		SessionConf sessionConf = SessionConf.getInstance();
		SessionCache cache = sessionConf.getCache();

		ValueSplit valueSplit = sessionConf.getValueSplit();
		if (valueSplit == null || !valueSplit.getFlag()) {
			return cache.get(key);
		} else {
			int size = parseValueSize(key);
			if (size <= 0) {
				cache.remove(key);
				return null;
			}

			StringBuilder builder = new StringBuilder("");
			for (int i = 0; i < size; i++) {
				String subVal = cache.get(key + "." + i);
				builder.append(subVal);
			}
			return builder.toString();
		}
	}

	public static void remove(String key) {
		SessionConf sessionConf = SessionConf.getInstance();
		SessionCache cache = sessionConf.getCache();

		ValueSplit valueSplit = sessionConf.getValueSplit();
		if (valueSplit == null || !valueSplit.getFlag()) {
			cache.remove(key);
		} else {
			int size = parseValueSize(key);
			if (size <= 0) {
				cache.remove(key);
				return;
			}

			cache.remove(key);
			for (int i = 0; i < size; i++) {
				cache.remove(key + "." + i);
			}
		}
	}

	private static int parseValueSize(String key) {
		SessionCache cache = SessionConf.getInstance().getCache();

		int size = 0;
		try {
			String val = cache.get(key);
			size = Integer.parseInt(val);
		} catch (Exception e) {
		}
		return size;
	}

	private static List<String> split(String value, int maxLength) {
		List<String> list = new ArrayList<String>();
		if (maxLength <= 0 || value.length() <= maxLength) {
			list.add(value);
			return list;
		}

		StringBuilder builder = new StringBuilder(value);
		while (builder.length() > maxLength) {
			list.add(builder.substring(0, maxLength));
			builder.delete(0, maxLength);
		}
		if (builder.length() > 0) {
			list.add(builder.toString());
		}
		return list;
	}
}
