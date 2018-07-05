package com.assist4j.schedule;


import com.assist4j.data.cache.Cache;


/**
 * @author yuwei
 */
public class RedisLeaderElector extends AbstractLeaderElector {
	private static final String CACHE_LEADER_KEY_PRE = "cache.schedule.leader.";


	private Cache cache;

	/**
	 * timeout in milliseconds
	 */
	private int timeout;

	/**
	 * redis key
	 */
	private String key;



	public RedisLeaderElector(Cache cache, int timeout, String prjNo) {
		this.cache = cache;
		this.timeout = timeout;
		this.key = CACHE_LEADER_KEY_PRE + prjNo;
	}

	@Override
	protected void registLeader(String node) {
		cache.put(key, node, timeout / 1000);
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
