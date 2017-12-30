package com.assist4j.data.cache;


/**
 * @author yuwei
 */
public interface CacheValue<T extends CacheValue<T>> {
	String encode();
	T decode(String value);
}
