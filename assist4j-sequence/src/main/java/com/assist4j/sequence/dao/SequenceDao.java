package com.assist4j.sequence.dao;


import com.assist4j.sequence.bean.SequenceHolder;


/**
 * @author yuwei
 */
public interface SequenceDao {
	default void init() {}
	default void destroy() {}
	
	void ensure(String seqName);
	SequenceHolder nextRange(String seqName);
}
