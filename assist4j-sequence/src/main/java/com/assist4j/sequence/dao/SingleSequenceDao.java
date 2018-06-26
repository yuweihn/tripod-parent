package com.assist4j.sequence.dao;


import javax.sql.DataSource;
import com.assist4j.sequence.bean.SequenceHolder;
import com.assist4j.sequence.exception.SequenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;


/**
 * @author yuwei
 */
public class SingleSequenceDao extends AbstractSequenceDao {
	private static final Logger log = LoggerFactory.getLogger(SingleSequenceDao.class);


	private DataSource dataSource;

	public SingleSequenceDao() {

	}

	@Override
	public void init() {
		Assert.notNull(dataSource, "The dataSource is null.");
		super.init();
	}

	@Override
	public void ensure(String seqName, long initValue) {
		if (initValue < 0) {
			initValue = 0;
		}

		Long oldValue = selectSeqValue(0, seqName);
		if (oldValue == null) {
			insertSeq(0, seqName, initValue);
		} else if (oldValue < initValue) {
			updateSeqValue(0, seqName, oldValue, initValue);
		}
	}

	@Override
	public SequenceHolder nextRange(String seqName) {
		Assert.notNull(seqName, "序列名称不能为空");

		int retryTimes = getRetryTimes();
		for (int i = 0; i < retryTimes + 1; ++i) {
			Long oldValue = selectSeqValue(0, seqName);

			if (oldValue == null || oldValue < 0L || oldValue > Long.MAX_VALUE - 100000000L) {
				throw new SequenceException("Invalid value, seqName = " + seqName + ", value = " + oldValue + ".");
			}

			Long newValue = oldValue + getInnerStep();
			try {
				updateSeqValue(0, seqName, oldValue, newValue);
			} catch(Exception e) {
				log.error("", e);
				continue;
			}
			return new SequenceHolder(oldValue + 1L, newValue);
		}
		throw new SequenceException("Retried too many times, retryTimes = " + retryTimes);
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	protected DataSource getDataSource(int segment) {
		return dataSource;
	}
}
