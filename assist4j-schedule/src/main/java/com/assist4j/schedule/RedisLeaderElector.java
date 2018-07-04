package com.assist4j.schedule;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author yuwei
 */
public class RedisLeaderElector implements LeaderElector {
	private static final Logger log = LoggerFactory.getLogger(RedisLeaderElector.class);
	/**
	 * root node
	 */
	private static final String EPHEMERAL_ROOT_NODE = "/Schedule_leader_";



	@Override
	public boolean isLeader() {
		return false;
	}

	@Override
	public String getNodeTag() {
		return null;
	}

	@Override
	public String getLeaderNodeTag() {
		return null;
	}

	@Override
	public void init() {

	}

	@Override
	public void destroy() {

	}
}
