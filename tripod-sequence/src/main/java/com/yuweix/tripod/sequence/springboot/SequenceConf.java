package com.yuweix.tripod.sequence.springboot;


import com.yuweix.tripod.sequence.base.DefaultSequence;
import com.yuweix.tripod.sequence.base.SequenceBeanFactory;
import com.yuweix.tripod.sequence.base.SequenceBeanHolder;
import com.yuweix.tripod.sequence.dao.SegmentSequenceDao;
import com.yuweix.tripod.sequence.dao.SequenceDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class SequenceConf {
	@ConditionalOnMissingBean(SequenceDao.class)
	@Bean(name = "sequenceDao")
	public SequenceDao sequenceDao(@Qualifier("dataSource") DataSource dataSource
			, @Value("${tripod.sequence.setting.innerStep:100}") int innerStep
			, @Value("${tripod.sequence.setting.retryTimes:5}") int retryTimes
			, @Value("${tripod.sequence.setting.segmentCount:1}") int segmentCount
			, @Value("${tripod.sequence.setting.maxSkipCount:5}") int maxSkipCount
			, @Value("${tripod.sequence.setting.maxWaitMillis:5000}") long maxWaitMillis
			, @Value("${tripod.sequence.setting.ruleClassName:}") String ruleClassName
			, @Value("${tripod.sequence.setting.tableName:sequence}") String tableName) {
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

	@ConditionalOnMissingBean(SequenceBeanHolder.class)
	@Bean(name = "sequenceBeanHolder")
	@ConfigurationProperties(prefix = "tripod.sequence", ignoreUnknownFields = true)
	public SequenceBeanHolder sequenceBeanHolder() {
		return new SequenceBeanHolder() {
			private Map<String, String> map = new HashMap<>();
			@Override
			public Map<String, String> getBeans() {
				return map;
			}
		};
	}

	@ConditionalOnMissingBean(SequenceBeanFactory.class)
	@Bean(name = "sequenceBeanFactory")
	public SequenceBeanFactory sequenceBeanFactory(Environment env) {
		String sequenceClz = env.getProperty("tripod.sequence.className");
		if (sequenceClz != null && !"".equals(sequenceClz)) {
			return new SequenceBeanFactory(sequenceClz);
		} else {
			return new SequenceBeanFactory(DefaultSequence.class);
		}
	}
}
