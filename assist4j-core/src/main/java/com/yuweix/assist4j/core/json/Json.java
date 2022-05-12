package com.yuweix.assist4j.core.json;


import com.alibaba.fastjson.TypeReference;


/**
 * @author yuwei
 */
public interface Json {
	<T>String serialize(T t);
	<T>T deserialize(String str);

	String toJSONString(Object object);
	<T>T parseObject(String text, TypeReference<T> type);
	<T>T parseObject(String text, Class<T> clazz);
}
