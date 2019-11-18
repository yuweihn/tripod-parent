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
    <T>boolean put(String key, T value, long timeout);

	/**
	 * @param key
	 * @return
	 */
    <T>T get(String key);

	/**
	 * @param key
	 */
	void remove(String key);
}
