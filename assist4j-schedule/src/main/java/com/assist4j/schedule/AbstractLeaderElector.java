package com.assist4j.schedule;


import com.assist4j.schedule.util.IpUtil;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author yuwei
 */
public abstract class AbstractLeaderElector implements LeaderElector {
	private String localNode;
	private Lock lock = new ReentrantLock();


	@Override
	public String getLocalNode() {
		if (localNode == null || "".equals(localNode.trim())) {
			lock.lock();
			try {
				if (null == localNode || "".equals(localNode.trim())) {
					this.localNode = IpUtil.getLocalInnerIP();
				}
			} finally {
				lock.unlock();
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
	 */
	abstract void assureLeader();


	@Override
	public boolean isLeader() {
		String localNode = getLocalNode();
		String leaderNode = getLeaderNode();
		if (leaderNode == null || "".equals(leaderNode)) {
			assureLeader();
		}

		leaderNode = getLeaderNode();
		return localNode.equals(leaderNode);
	}
}
