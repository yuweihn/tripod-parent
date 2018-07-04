package com.assist4j.schedule;




/**
 * @author yuwei
 */
public interface LeaderElector {
	boolean isLeader();
	String getNodeTag();
	String getLeaderNodeTag();

	void init();
	void destroy();
}
