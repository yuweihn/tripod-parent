package com.yuweix.assist4j.core.json;


import com.alibaba.fastjson2.*;
import com.alibaba.fastjson2.filter.Filter;

import java.util.LinkedList;
import java.util.List;


/**
 * @author yuwei
 */
public class Fastjson implements Json {
	private final List<String> autoTypes = new LinkedList<>();
	private Filter autoTypeFilter;

	@Override
	public void addAccept(String name) {
		autoTypes.add(name);
		autoTypeFilter = JSONReader.autoTypeFilter(autoTypes.toArray(new String[0]));
	}

	@Override
	public <T> String serialize(T t) {
		if (t == null) {
			return null;
		}
		return JSON.toJSONString(t, JSONWriter.Feature.WriteClassName);
	}

	@Override
	public <T>T deserialize(String str) {
		if (str == null) {
			return null;
		}
		if (autoTypeFilter == null) {
			return JSON.parseObject(str, new TypeReference<T>() {});
		} else {
			return JSON.parseObject(str, new TypeReference<T>() {}, autoTypeFilter);
		}
	}

	@Override
	public String toJSONString(Object object) {
		return JSON.toJSONString(object);
	}

	@Override
	public <T>T parseObject(String text, TypeReference<T> type) {
		return JSON.parseObject(text, type);
	}

	@Override
	public <T>T parseObject(String text, Class<T> clz) {
		return JSON.parseObject(text, clz);
	}
}
