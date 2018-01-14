package com.assist4j.sequence.dao.loadbalancer;


/**
 * 多数据源时的路由转发规则
 * @author yuwei
 */
public interface IRule {
	/**
	 * 选择合适的数据库分片
	 */
	int chooseSegment();
	
	void setSegmentCount(int segmentCount);
}
