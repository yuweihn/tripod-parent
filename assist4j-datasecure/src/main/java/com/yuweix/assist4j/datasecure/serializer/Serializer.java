package com.yuweix.assist4j.datasecure.serializer;




/**
 * @author yuwei
 */
public interface Serializer {
	<T>String serialize(T t);
	<T>T deserialize(String str);
}
