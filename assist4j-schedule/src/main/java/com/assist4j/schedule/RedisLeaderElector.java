package com.assist4j.schedule;


import com.assist4j.data.cache.Cache;
import com.assist4j.schedule.util.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yuwei
 */
public class RedisLeaderElector implements LeaderElector {
	private static final Logger log = LoggerFactory.getLogger(RedisLeaderElector.class);
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


	private String nodeTag;



	public RedisLeaderElector(Cache cache, int timeout, String projectNo) {
		this.cache = cache;
		this.timeout = timeout;
		this.projectNo = CACHE_LEADER_KEY_PRE + projectNo;
	}

	@Override
	public synchronized boolean isLeader() {
		String thisNodeTag = getNodeTag();
		String leaderNoteTag = cache.get(projectNo);
		if (leaderNoteTag == null || "".equals(leaderNoteTag)) {
			cache.put(projectNo, thisNodeTag, timeout / 1000);
		}

		leaderNoteTag = cache.get(projectNo);
		return thisNodeTag.equals(leaderNoteTag);
	}

	@Override
	public String getNodeTag() {
		if (null == nodeTag || "".equals(nodeTag.trim())) {
			synchronized(this) {
				if (null == nodeTag || "".equals(nodeTag.trim())) {
					this.nodeTag = IpUtil.getLocalInnerIP();
				}
			}
		}
		return nodeTag;
	}

	public void setNodeTag(String nodeTag) {
		this.nodeTag = nodeTag;
	}

	@Override
	public String getLeaderNodeTag() {
		return cache.get(projectNo);
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
