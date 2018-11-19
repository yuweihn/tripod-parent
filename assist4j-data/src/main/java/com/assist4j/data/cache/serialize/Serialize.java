package com.assist4j.data.cache.serialize;





/**
 * @author yuwei
 */
public interface Serialize {
	<T>String encode(T t);
	<T>T decode(String str);
}
