package com.assist4j.data.cache;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.ArrayList;
import java.util.List;


/**
 * @author yuwei
 */
public abstract class AbstractCache implements Cache {
	@Override
	public <T> boolean putSplit(String key, T value, long timeout, int maxLength) {
		if (value.getClass() == String.class) {
			return putSplit0(key, (String) value, timeout, maxLength);
		} else {
			return putSplit0(key, JSON.toJSONString(value), timeout, maxLength);
		}
	}
	private boolean putSplit0(String key, String value, long timeout, int maxLength) {
		int oldSize = parseValueSize(get(key));
		List<String> valList = split(value, maxLength);
		int newSize = valList.size();
		boolean b = put(key, "" + newSize, timeout);
		for (int i = 0; i < newSize; i++) {
			b &= put(key + "." + i, valList.get(i), timeout + 60);
		}

		for (int i = newSize; i < oldSize; i++) {
			remove(key + "." + i);
		}
		return b;
	}

	@Override
	public String getSplit(String key) {
		int size = parseValueSize(get(key));
		if (size <= 0) {
			remove(key);
			return null;
		}

		StringBuilder builder = new StringBuilder("");
		for (int i = 0; i < size; i++) {
			String subVal = get(key + "." + i);
			builder.append(subVal);
		}
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getSplit(String key, Class<T> clz) {
		String val = getSplit(key);
		if (val == null) {
			return null;
		}
		return clz == String.class ? (T) val : JSON.parseObject(val, clz);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getSplit(String key, TypeReference<T> type) {
		String val = getSplit(key);
		if (val == null) {
			return null;
		}
		return type.getType() == String.class ? (T) val : JSON.parseObject(val, type);
	}

	@Override
	public void removeSplit(String key) {
		int size = parseValueSize(get(key));
		if (size <= 0) {
			remove(key);
			return;
		}

		remove(key);
		for (int i = 0; i < size; i++) {
			remove(key + "." + i);
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
