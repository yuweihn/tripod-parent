package com.yuweix.assist4j.dao.mybatis;




/**
 * @author yuwei
 */
public interface Cacheable {
	<T>T get(String key);
	<T>boolean put(String key, T value);
	void remove(String key);
}
