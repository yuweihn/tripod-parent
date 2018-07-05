package com.assist4j.schedule;




/**
 * @author yuwei
 */
public interface LeaderElector {
	boolean isLeader();
	String getNodeName();
	String getLeaderNodeName();

	void init();
	void destroy();
}
