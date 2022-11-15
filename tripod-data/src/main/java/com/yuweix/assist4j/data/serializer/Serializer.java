package com.yuweix.tripod.data.serializer;




/**
 * @author yuwei
 */
public interface Serializer {
	<T>String serialize(T t);
	<T>T deserialize(String str);
}
