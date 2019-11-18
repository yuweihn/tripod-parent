package com.assist4j.data.cache;




/**
 * @author yuwei
 */
public interface Cache {
	/**
	 * @param key
	 * @return
	 */
	boolean contains(String key);
	
	/**
	 * @param key
	 * @param value
	 * @param timeout 过期时间(s)。
	 * @return
	 */
	boolean put(String key, String value, long timeout);
	
	/**
	 * @param key
	 * @return
	 */
	String get(String key);
	
	/**
	 * @param key
	 */
	void remove(String key);
}
