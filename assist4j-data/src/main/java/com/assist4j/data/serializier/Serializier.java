package com.assist4j.data.serializier;




/**
 * @author yuwei
 */
public interface Serializier {
	<T>String serialize(T t);
	<T>T deserialize(String str);
}
