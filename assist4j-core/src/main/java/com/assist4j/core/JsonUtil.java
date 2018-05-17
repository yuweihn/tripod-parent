package com.assist4j.core;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;


/**
 * @author yuwei
 */
public abstract class JsonUtil {
	@SuppressWarnings("unchecked")
	public static<T> T toBean(String text, Class<?> clz) {
		if (text == null || "".equals(text)) {
			return null;
		}
		return (T) JSONObject.parseObject(text, clz);
	}

	@SuppressWarnings("unchecked")
	public static<T> List<T> toBeanList(String text, Class<?> clz) {
		return (List<T>) JSONObject.parseArray(text, clz);
	}

	public static String toJson(Object obj) {
		if (obj == null) {
			return null;
		}
		return JSONObject.toJSONString(obj);
	}

	public static<T> String toJson(List<T> list) {
		if(CollectionUtils.isEmpty(list)) {
			return "[]";
		}

		return JSONObject.toJSONString(list);
	}

	public static boolean isJson(String text) {
		try {
			JSONObject jsonObject = JSONObject.parseObject(text);
			return jsonObject != null;
		} catch (Exception e) {
			try {
				JSONArray array = JSONObject.parseArray(text);
				return array != null;
			} catch (Exception e1) {
				return false;
			}
		}
	}

	public static<K, V, T> T mapToObject(Map<K, V> map, Class<T> clz) {
		return toBean(toJson(map), clz);
	}

	public static<K, V, T> Map<K, V> objectToMap(T t) {
		return toBean(toJson(t), Map.class);
	}
}
