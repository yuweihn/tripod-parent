package com.yuweix.tripod.core.json;


import com.alibaba.fastjson2.TypeReference;


/**
 * @author yuwei
 */
public interface Json {
	void addAccept(String name);
	<T>String serialize(T t);
	<T>T deserialize(String str);

	String toJSONString(Object object);
	Object parse(String text);
	<T>T parseObject(String text, TypeReference<T> type);
	<T>T parseObject(String text, Class<T> clazz);
}
