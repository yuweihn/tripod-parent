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
	protected void createLeaderNode(String node) {
		boolean b = redis.put(key, node, timeout / 1000);
		if (!b) {
			throw new RuntimeException("Error happened in cache.");
		}
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

	}
}
