package com.yuweix.assist4j.data.elect;


import java.io.Serializable;


/**
 * Leader选择器
 * @author yuwei
 */
public interface Elector {
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
		r.setLocal(localNode);
		return r;
	}

	void release(String lock);
	String getLocalNode();

	class R implements Serializable {
		private static final long serialVersionUID = 1L;
		private boolean success;
		private String leader;
		private String local;

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

		public void setLocal(String local) {
			this.local = local;
		}

		public String getLocal() {
			return local;
		}
	}
}
