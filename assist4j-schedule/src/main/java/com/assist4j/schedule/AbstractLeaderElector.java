package com.assist4j.schedule;


import com.assist4j.schedule.util.IpUtil;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author yuwei
 */
public abstract class AbstractLeaderElector implements LeaderElector {
	private String localNode;
	private Lock localNodeLock = new ReentrantLock();
	private Lock createLeaderNodeLock = new ReentrantLock();


	@Override
	public String getLocalNode() {
		if (localNode == null || "".equals(localNode.trim())) {
			localNodeLock.lock();
			try {
				if (null == localNode || "".equals(localNode.trim())) {
					this.localNode = IpUtil.getLocalInnerIP();
				}
			} finally {
				localNodeLock.unlock();
			}
		}
		return localNode;
	}

	public void setLocalNode(String localNode) {
		this.localNode = localNode;
	}

	/**
	 * 确保elector上保存了leader。
	 * 若没有，将当前机器存储在elector的指定节点上。
	 * 然后返回leader
	 */
	private String assureLeader() {
		String leaderNode = getLeaderNode();
		if (leaderNode == null || "".equals(leaderNode)) {
			try {
				createLeaderNodeLock.lock();
				leaderNode = getLeaderNode();
				if (leaderNode == null || "".equals(leaderNode)) {
					String localNode = getLocalNode();
					createLeaderNode(localNode);
					return localNode;
				} else {
					return leaderNode;
				}
			} finally {
				createLeaderNodeLock.unlock();
			}
		} else {
			return leaderNode;
		}
	}

	abstract void createLeaderNode(String node);


	@Override
	public boolean isLeader() {
		return getLocalNode().equals(assureLeader());
	}
}
