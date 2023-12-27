package com.yuweix.tripod.sequence.base;


import com.yuweix.tripod.sequence.bean.SequenceHolder;
import com.yuweix.tripod.sequence.dao.SequenceDao;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author yuwei
 */
public abstract class AbstractSequence implements Sequence {
	protected final Lock lock = new ReentrantLock();
	protected volatile SequenceHolder sequenceHolder;

	@SeqField(isDao = true)
	protected SequenceDao sequenceDao;
	@SeqField(isName = true)
	protected String name;
	@SeqField(isMinValue = true)
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
