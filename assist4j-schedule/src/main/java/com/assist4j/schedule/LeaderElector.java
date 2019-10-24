package com.assist4j.schedule;




/**
 * @author yuwei
 */
public interface LeaderElector {
	boolean isLeader();
	String getLocalNode();
	String getLeaderNode();
}
