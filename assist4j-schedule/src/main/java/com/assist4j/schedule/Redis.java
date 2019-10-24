package com.assist4j.schedule;




/**
 * @author yuwei
 */
public interface Redis {
	/**
	 * @param expiredTime 单位：秒。
	 */
	boolean lock(String key, String owner, long expiredTime);
	boolean unlock(String key, String owner);
	String get(String key);
}
