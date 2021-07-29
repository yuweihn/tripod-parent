package com.yuweix.assist4j.sequence.springboot;


import com.yuweix.assist4j.sequence.base.DefaultSequence;
import com.yuweix.assist4j.sequence.base.SequenceBeanFactory;
import com.yuweix.assist4j.sequence.base.SequenceBeanHolder;
import com.yuweix.assist4j.sequence.dao.SegmentSequenceDao;
import com.yuweix.assist4j.sequence.dao.SequenceDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class SequenceConf {
	@ConditionalOnMissingBean(name = "sequenceDao")
	@Bean(name = "sequenceDao", initMethod = "init", destroyMethod = "destroy")
	public SequenceDao sequenceDao(@Qualifier("dataSource") DataSource dataSource
			, @Value("${assist4j.sequence-setting.innerStep:100}") int innerStep
			, @Value("${assist4j.sequence-setting.retryTimes:5}") int retryTimes
			, @Value("${assist4j.sequence-setting.segmentCount:1}") int segmentCount
			, @Value("${assist4j.sequence-setting.maxSkipCount:5}") int maxSkipCount
			, @Value("${assist4j.sequence-setting.maxWaitMillis:5000}") long maxWaitMillis
			, @Value("${assist4j.sequence-setting.ruleClassName:}") String ruleClassName
			, @Value("${assist4j.sequence-setting.tableName:sequence}") String tableName) {
		SegmentSequenceDao sequenceDao = new SegmentSequenceDao();
		sequenceDao.setDataSource(dataSource);
		sequenceDao.setInnerStep(innerStep);
		sequenceDao.setRetryTimes(retryTimes);
		sequenceDao.setSegmentCount(segmentCount);
		sequenceDao.setMaxSkipCount(maxSkipCount);
		sequenceDao.setMaxWaitMillis(maxWaitMillis);
		sequenceDao.setRuleClassName(ruleClassName);
		sequenceDao.setTableName(tableName);
		return sequenceDao;
	}

	@ConditionalOnMissingBean(name = "sequenceBeanHolder")
	@Bean(name = "sequenceBeanHolder")
	@ConfigurationProperties(prefix = "assist4j", ignoreUnknownFields = true)
	public SequenceBeanHolder sequenceBeanHolder() {
		return new SequenceBeanHolder() {
			private Map<String, String> sequence = new HashMap<String, String>();

			@Override
			public Map<String, String> getSequenceMap() {
				return sequence;
			}
		};
	}

	@ConditionalOnMissingBean(name = "sequenceBeanFactory")
	@Bean(name = "sequenceBeanFactory")
	public SequenceBeanFactory sequenceBeanFactory() {
		return new SequenceBeanFactory(DefaultSequence.class, "sequenceBeanHolder");
	}
}
