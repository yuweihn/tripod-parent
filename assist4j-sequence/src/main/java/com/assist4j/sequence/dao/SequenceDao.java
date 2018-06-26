package com.assist4j.sequence.dao;


import com.assist4j.sequence.bean.SequenceHolder;


/**
 * @author yuwei
 */
public interface SequenceDao {
	void init();
	void destroy();

	void ensure(String seqName, long minValue);
	SequenceHolder nextRange(String seqName);
}
