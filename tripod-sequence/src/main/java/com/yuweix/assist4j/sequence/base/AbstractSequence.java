package com.yuweix.tripod.sequence.base;


import com.yuweix.tripod.sequence.bean.SequenceHolder;
import com.yuweix.tripod.sequence.dao.SequenceDao;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author yuwei
 */
public abstract class AbstractSequence implements Sequence {
	protected final Lock lock = new ReentrantLock();
	protected volatile SequenceHolder sequenceHolder;

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
		synchronized(this) {
			sequenceDao.ensure(name, minValue);
		}
	}

	@PreDestroy
	public void destroy() {

	}

	@Override
	public long next() {
		ensureSequenceHolder();
		return nextVal();
	}

	protected abstract long nextVal();

	private void ensureSequenceHolder() {
		if (sequenceHolder != null) {
			return;
		}
		lock.lock();
		try {
			if (sequenceHolder == null) {
				sequenceHolder = sequenceDao.nextRange(name);
			}
		} finally {
			lock.unlock();
		}
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
