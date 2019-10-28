package com.assist4j.schedule;




/**
 * @author yuwei
 */
public class RedisLeaderElector extends AbstractLeaderElector {
	private static final String CACHE_LEADER_KEY_PRE = "cache.schedule.leader.";


	private Redis redis;

	/**
	 * timeout in milliseconds
	 */
	private int timeout;

	/**
	 * leader key in redis
	 */
	private String key;



	public RedisLeaderElector(Redis redis, int timeout, String prjNo) {
		this.redis = redis;
		this.timeout = timeout;
		this.key = CACHE_LEADER_KEY_PRE + prjNo;
	}

	@Override
	public boolean isLeader() {
		return redis.lock(key, getLocalNode(), timeout / 1000);
	}

	@Override
	public String getLeaderNode() {
		return redis.get(key);
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {
		redis.unlock(key, getLocalNode());
	}
}
