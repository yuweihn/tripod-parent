package com.assist4j.session;


import com.assist4j.session.cache.SessionCache;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yuwei
 */
public abstract class SplitCacheUtil {
	/**
	 * timeout 有效期(单位：秒)。
	 */
	public static boolean put(String key, String value, long timeout, int maxLength, SessionCache cache) {
		List<String> valList = split(value, maxLength);
		int newSize = valList.size();
		boolean b = cache.put(key, "" + newSize, timeout);
		for (int i = 0; i < newSize; i++) {
			b &= cache.put(key + "." + i, valList.get(i), timeout + 60);
		}

		int oldSize = parseValueSize(cache.get(key));
		for (int i = newSize; i < oldSize; i++) {
			cache.remove(key + "." + i);
		}
		return b;
	}

	public static String get(String key, SessionCache cache) {
		int size = parseValueSize(cache.get(key));
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

	public static void remove(String key, SessionCache cache) {
		int size = parseValueSize(cache.get(key));
		if (size <= 0) {
			cache.remove(key);
			return;
		}

		cache.remove(key);
		for (int i = 0; i < size; i++) {
			cache.remove(key + "." + i);
		}
	}


	private static int parseValueSize(String val) {
		if (val == null) {
			return 0;
		}

		try {
			return Integer.parseInt(val);
		} catch (Exception e) {
			return 0;
		}
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
