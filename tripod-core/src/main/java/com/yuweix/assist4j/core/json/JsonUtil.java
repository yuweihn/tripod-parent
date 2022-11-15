package com.yuweix.assist4j.core.json;


import com.alibaba.fastjson2.TypeReference;


/**
 * @author yuwei
 */
public class JsonUtil {
	private static Json json;

	private JsonUtil(Json json) {
		JsonUtil.json = json;
	}

	public static String toJSONString(Object object) {
		return json.toJSONString(object);
	}
	public static<T> T parseObject(String text, TypeReference<T> type) {
		return json.parseObject(text, type);
	}
	public static<T> T parseObject(String text, Class<T> clazz) {
		return json.parseObject(text, clazz);
	}

	public static void addAccept(String name) {
		json.addAccept(name);
	}
}
