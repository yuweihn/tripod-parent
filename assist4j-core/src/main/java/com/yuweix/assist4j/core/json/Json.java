package com.yuweix.assist4j.core.json;




/**
 * @author yuwei
 */
public interface Json {
	<T>String toString(T t);
	<T>T toObject(String str);
}
