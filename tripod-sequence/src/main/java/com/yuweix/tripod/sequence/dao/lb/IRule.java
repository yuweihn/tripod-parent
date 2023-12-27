package com.yuweix.tripod.sequence.dao.lb;


/**
 * 多数据源时的路由转发规则
 * @author yuwei
 */
public interface IRule {
	/**
	 * 选择合适的数据库分片
	 */
	int chooseSegment(String seqName);
	
	void setSegmentCount(int segmentCount);
}
