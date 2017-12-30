package org.assist4j.sequence.base;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.assist4j.sequence.dao.SequenceDao;

import lombok.extern.slf4j.Slf4j;
import org.assist4j.sequence.bean.SequenceHolder;


/**
 * @author yuwei
 */
@Slf4j
public class DefaultSequence implements Sequence {
	private final Lock lock = new ReentrantLock();
	private SequenceDao sequenceDao;
	private String name;
	private volatile SequenceHolder sequenceHolder;

	public DefaultSequence() {}

	@Override
	public void init() {
		synchronized(this) {
			sequenceDao.ensure(name);
		}
	}

	@Override
	public long nextValue() {
		if (sequenceHolder == null) {
			lock.lock();
			try {
				if (sequenceHolder == null) {
					sequenceHolder = sequenceDao.nextRange(name);
				}
			} finally {
				lock.unlock();
			}
		}

		long value = sequenceHolder.getAndIncrement();
		if (value <= 0L) {
			lock.lock();
			try {
				do {
					sequenceHolder = sequenceDao.nextRange(name);
					value = sequenceHolder.getAndIncrement();
				} while(value <= 0L);
			} finally {
				lock.unlock();
			}
		}
		
		return value;
	}

	@Override
	public void destroy() {
		if (sequenceHolder == null) {
			return;
		}
		
		long curValue = sequenceHolder.get();
		log.info("{}: {}", name, curValue);
		//TODO
		
		sequenceHolder = null;
	}

	public void setSequenceDao(SequenceDao sequenceDao) {
		this.sequenceDao = sequenceDao;
	}

	public void setName(String name) {
		this.name = name;
	}
}
