package com.assist4j.schedule;




/**
 * @author yuwei
 */
public interface Redis {
	/**
	 * 设置缓存中指定key的值
	 * @param key 缓存key。
	 * @param value 缓存的值。
	 * @param expiredTime 缓存过期的秒数。
	 * @return true更新成功，false更新失败。
	 */
	boolean put(String key, String value, long expiredTime);
	String get(String key);
}
