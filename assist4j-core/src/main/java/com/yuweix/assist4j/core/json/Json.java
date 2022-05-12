package com.yuweix.assist4j.core.json;




/**
 * @author yuwei
 */
public interface Json {
	<T>String serialize(T t);
	<T>T deserialize(String str);
}
