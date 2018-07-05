package com.assist4j.schedule;


import com.assist4j.schedule.util.IpUtil;


/**
 * @author yuwei
 */
public abstract class AbstractLeaderElector implements LeaderElector {
	private String nodeName;


	@Override
	public String getNodeName() {
		if (nodeName == null || "".equals(nodeName.trim())) {
			synchronized(this) {
				if (null == nodeName || "".equals(nodeName.trim())) {
					this.nodeName = IpUtil.getLocalInnerIP();
				}
			}
		}
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
}
