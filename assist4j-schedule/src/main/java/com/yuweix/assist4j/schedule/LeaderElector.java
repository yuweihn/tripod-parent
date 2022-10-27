package com.yuweix.assist4j.schedule;


import java.io.Serializable;


/**
 * Leader选择器
 * @author yuwei
 */
public interface LeaderElector {
	/**
	 * 尝试获取锁，返回锁的持有者。
	 * @param lock
	 * @return
	 */
	String acquire(String lock);
	default R tryAcquire(String lock) {
		String leader = acquire(lock);
		String localNode = getLocalNode();
		R r = new R();
		r.setSuccess(localNode.equals(leader));
		r.setLeader(leader);
		return r;
	}

	void release(String lock);
	String getLocalNode();

	class R implements Serializable {
		private static final long serialVersionUID = 1L;
		private boolean success;
		private String leader;

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public String getLeader() {
			return leader;
		}

		public void setLeader(String leader) {
			this.leader = leader;
		}
	}
}
