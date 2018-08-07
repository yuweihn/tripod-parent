package com.assist4j.data.cache;




/**
 * 分布式锁
 * @author yuwei
 */
public interface DistLock {
	boolean lock();
	void unlock();
}
