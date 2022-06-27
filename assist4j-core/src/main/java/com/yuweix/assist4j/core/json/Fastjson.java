package com.yuweix.assist4j.core.json;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;


/**
 * @author yuwei
 */
public class Fastjson implements Json {
	@Override
	public void addAccept(String name) {
		JSONFactory.getDefaultObjectReaderProvider().addAutoTypeAccept(name);
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
		return JSON.parseObject(str, new TypeReference<T>() {});
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
