package com.assist4j.schedule;


import com.assist4j.data.cache.Cache;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author yuwei
 */
public class RedisLeaderElector extends AbstractLeaderElector {
	private static final String CACHE_LEADER_KEY_PRE = "cache.schedule.leader.";

	private Lock registLeaderLock = new ReentrantLock();


	private Cache cache;

	/**
	 * timeout in milliseconds
	 */
	private int timeout;

	/**
	 * redis key
	 */
	private String key;



	public RedisLeaderElector(Cache cache, int timeout, String projectNo) {
		this.cache = cache;
		this.timeout = timeout;
		this.key = CACHE_LEADER_KEY_PRE + projectNo;
	}

	@Override
	protected void assureLeader() {
		String leaderNode = getLeaderNode();
		if (leaderNode == null || "".equals(leaderNode)) {
			try {
				registLeaderLock.lock();
				leaderNode = getLeaderNode();
				if (leaderNode == null || "".equals(leaderNode)) {
					String localNode = getLocalNode();
					cache.put(key, localNode, timeout / 1000);
				}
			} finally {
				registLeaderLock.unlock();
			}
		}
	}

	@Override
	public String getLeaderNode() {
		return cache.get(key);
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
