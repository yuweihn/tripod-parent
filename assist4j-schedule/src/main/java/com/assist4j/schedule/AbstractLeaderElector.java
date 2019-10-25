package com.assist4j.schedule;


import com.assist4j.schedule.util.IpUtil;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author yuwei
 */
public abstract class AbstractLeaderElector implements LeaderElector {
	private final Lock localNodeLock = new ReentrantLock();
	private String localNode;


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

		if (localNode == null || "".equals(localNode.trim())) {
			throw new RuntimeException("[localNode] can't be empty.");
		}
		return localNode;
	}

	public void setLocalNode(String localNode) {
		if (localNode == null || "".equals(localNode.trim())) {
			throw new RuntimeException("[localNode] can't be empty.");
		}
		this.localNode = localNode;
	}

	/**
	 * 将当前节点作为leader存储在elector的指定节点上。
	 */
	abstract boolean createLeaderNode();

	@Override
	public boolean isLeader() {
		return getLocalNode().equals(getLeaderNode()) || createLeaderNode();
	}

	abstract void init();
	abstract void destroy();
}
