package com.yuweix.assist4j.core.json;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;


/**
 * @author yuwei
 */
public class Fastjson implements Json {
	@Override
	public void addAccept(String name) {
		ParserConfig.getGlobalInstance().addAccept(name);
	}

	@Override
	public void addDeny(String name) {
		ParserConfig.getGlobalInstance().addDeny(name);
	}

	@Override
	public <T> String serialize(T t) {
		if (t == null) {
			return null;
		}
		return JSON.toJSONString(t, SerializerFeature.WriteClassName);
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
