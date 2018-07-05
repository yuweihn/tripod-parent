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
	 * task type flag
	 */
	private String projectNo;



	public RedisLeaderElector(Cache cache, int timeout, String projectNo) {
		this.cache = cache;
		this.timeout = timeout;
		this.projectNo = CACHE_LEADER_KEY_PRE + projectNo;
	}

	@Override
	public synchronized boolean isLeader() {
		String thisNodeName = getNodeName();
		String leaderNoteTag = cache.get(projectNo);
		if (leaderNoteTag == null || "".equals(leaderNoteTag)) {
			cache.put(projectNo, thisNodeName, timeout / 1000);
		}

		leaderNoteTag = cache.get(projectNo);
		return thisNodeName.equals(leaderNoteTag);
	}

	@Override
	public String getLeaderNodeName() {
		return cache.get(projectNo);
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
