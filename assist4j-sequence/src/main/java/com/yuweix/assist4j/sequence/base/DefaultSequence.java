package com.yuweix.assist4j.sequence.base;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.yuweix.assist4j.sequence.bean.SequenceHolder;


/**
 * @author yuwei
 */
public class DefaultSequence extends AbstractSequence {
	private final Lock lock = new ReentrantLock();
	private volatile SequenceHolder sequenceHolder;


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
}
