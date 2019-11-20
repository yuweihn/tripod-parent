package com.assist4j.data.cache;




/**
 * @author yuwei
 */
public interface Lock {
	<T>boolean lock(String key, T owner, long timeout);
	/**
	 * @param key
	 * @param owner
	 * @param timeout 单位：秒。
	 * @param reentrant   是否可重入
	 * @return
	 */
	<T>boolean lock(String key, T owner, long timeout, boolean reentrant);
	<T>boolean unlock(String key, T owner);
}
