package com.yuweix.assist4j.data.elect;




/**
 * @author yuwei
 */
public class RedisElector extends AbstractElector {
	private static final String CACHE_LEADER_KEY_PRE = "cache%s.elect.leader.";

	private Redis redis;

	/**
	 * timeout in milliseconds
	 */
	private int timeout;

	private String appName;


	public RedisElector(Redis redis, int timeout) {
		this(redis, timeout, null);
	}
	public RedisElector(Redis redis, int timeout, String appName) {
		super();
		this.redis = redis;
		this.timeout = timeout;
		this.appName = appName == null ? null : appName.trim();
	}

	@Override
	public String acquire(String lock) {
		String key = String.format(CACHE_LEADER_KEY_PRE
				, this.appName == null || "".equals(this.appName) ? "" : "." + this.appName) + lock;
		return redis.lock(key, getLocalNode(), timeout / 1000);
	}

	@Override
	public void release(String lock) {
		String key = String.format(CACHE_LEADER_KEY_PRE
				, this.appName == null || "".equals(this.appName) ? "" : "." + this.appName) + lock;
		redis.unlock(key, getLocalNode());
	}
}
