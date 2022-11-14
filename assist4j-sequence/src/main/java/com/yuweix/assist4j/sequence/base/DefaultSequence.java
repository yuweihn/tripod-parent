package com.yuweix.assist4j.sequence.base;



/**
 * @author yuwei
 */
public class DefaultSequence extends AbstractSequence {
	@Override
	public long nextVal() {
		long value = sequenceHolder.getAndIncrement();
		if (value <= 0L) {
			lock.lock();
			try {
				do {
					sequenceHolder = sequenceDao.nextRange(name);
					value = sequenceHolder.getAndIncrement();
				} while (value <= 0L);
			} finally {
				lock.unlock();
			}
		}
		return value;
	}
}
