package org.assist4j.sequence.dao;


import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.assist4j.sequence.bean.SequenceHolder;
import org.assist4j.sequence.exception.SequenceException;
import org.springframework.util.Assert;


/**
 * @author yuwei
 */
@Slf4j
public class SingleSequenceDao extends AbstractSequenceDao {
	private DataSource dataSource;

	public SingleSequenceDao() {}

	@Override
	public void init() {
		Assert.notNull(dataSource, "The dataSource is null.");
		super.init();
	}

	@Override
	public void ensure(String seqName) {
		Long value = selectSeqValue(0, seqName);
		if(value == null) {
			insertSeq(0, seqName, 0);
		}
	}

	@Override
	public SequenceHolder nextRange(String seqName) {
		Assert.notNull(seqName, "序列名称不能为空");

		int retryTimes = getRetryTimes();
		for(int i = 0; i < retryTimes + 1; ++i) {
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
