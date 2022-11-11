package com.yuweix.assist4j.sequence.base;


import com.yuweix.assist4j.sequence.dao.SequenceDao;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


/**
 * @author yuwei
 */
public abstract class AbstractSequence implements Sequence {
	@AnnSequenceDao
	protected SequenceDao sequenceDao;
	@AnnSequenceName
	protected String name;
	@AnnSequenceMinValue
	protected long minValue;


	public AbstractSequence() {

	}

	@PostConstruct
	public void init() {

	}

	@PreDestroy
	public void destroy() {

	}

	public void setSequenceDao(SequenceDao sequenceDao) {
		this.sequenceDao = sequenceDao;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMinValue(long minValue) {
		this.minValue = minValue;
	}
}
