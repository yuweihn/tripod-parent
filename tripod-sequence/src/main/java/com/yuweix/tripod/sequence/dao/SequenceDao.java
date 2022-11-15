package com.yuweix.tripod.sequence.dao;


import com.yuweix.tripod.sequence.bean.SequenceHolder;


/**
 * @author yuwei
 */
public interface SequenceDao {
	void ensure(String seqName, long minValue);
	SequenceHolder nextRange(String seqName);
}
