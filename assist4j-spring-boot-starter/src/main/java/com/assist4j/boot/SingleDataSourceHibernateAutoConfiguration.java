package com.assist4j.boot;


import com.alibaba.druid.pool.DruidDataSource;
import com.assist4j.data.springboot.SingleDataSourceHibernateConf;
import com.assist4j.sequence.base.DefaultSequence;
import com.assist4j.sequence.base.SequenceBeanFactory;
import com.assist4j.sequence.base.SequenceBeanHolder;
import com.assist4j.sequence.dao.SegmentSequenceDao;
import com.assist4j.sequence.dao.SequenceDao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "assist4j.boot.datasource", havingValue = "hibernate.single")
@Import({SingleDataSourceHibernateConf.class})
public class SingleDataSourceHibernateAutoConfiguration {

	@ConditionalOnMissingBean(name = "dataSource")
	@Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
	public DruidDataSource druidDataSourceMaster(@Value("${jdbc.driver.class}") String driverClassName
			, @Value("${jdbc.url}") String url
			, @Value("${jdbc.username}") String userName
			, @Value("${jdbc.password}") String password
			, @Value("${jdbc.default.read.only:false}") boolean defaultReadOnly
			, @Value("${jdbc.filters:stat}") String filters
			, @Value("${jdbc.max.active:2}") int maxActive
			, @Value("${jdbc.initial.size:1}") int initialSize
			, @Value("${jdbc.max.wait:60000}") long maxWaitMillis
			, @Value("${jdbc.remove.abandoned:true}") boolean removeAbandoned
			, @Value("${jdbc.remove.abandoned.timeout:1800}") int removeAbandonedTimeout
			, @Value("${jdbc.min.idle:1}") int minIdle
			, @Value("${jdbc.time.between.eviction.runs.millis:60000}") long timeBetweenEvictionRunsMillis
			, @Value("${jdbc.min.evictable.idle.time.millis:300000}") long minEvictableIdleTimeMillis
			, @Value("${jdbc.validation.query:select 'x'}") String validationQuery
			, @Value("${jdbc.test.while.idle:true}") boolean testWhileIdle
			, @Value("${jdbc.test.on.borrow:false}") boolean testOnBorrow
			, @Value("${jdbc.test.on.return:false}") boolean testOnReturn
			, @Value("${jdbc.pool.prepared.statements:true}") boolean poolPreparedStatements
			, @Value("${jdbc.max.pool.prepared.statement.per.connection.size:50}") int maxPoolPreparedStatementPerConnectionSize
			, @Value("${jdbc.max.open.prepared.statements:100}") int maxOpenPreparedStatements) throws SQLException {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setUrl(url);
		dataSource.setUsername(userName);
		dataSource.setPassword(password);
		dataSource.setDefaultReadOnly(defaultReadOnly);
		dataSource.setFilters(filters);
		dataSource.setMaxActive(maxActive);
		dataSource.setInitialSize(initialSize);
		dataSource.setMaxWait(maxWaitMillis);
		dataSource.setRemoveAbandoned(removeAbandoned);
		dataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		dataSource.setMinIdle(minIdle);
		dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		dataSource.setValidationQuery(validationQuery);
		dataSource.setTestWhileIdle(testWhileIdle);
		dataSource.setTestOnBorrow(testOnBorrow);
		dataSource.setTestOnReturn(testOnReturn);
		dataSource.setPoolPreparedStatements(poolPreparedStatements);
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
		dataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
		return dataSource;
	}

	@ConditionalOnMissingBean(name = "sequenceDao")
	@Bean(name = "sequenceDao", initMethod = "init", destroyMethod = "destroy")
	public SequenceDao sequenceDao(@Qualifier("dataSource") DataSource dataSource
			, @Value("${global.sequence.innerStep:100}") int innerStep
			, @Value("${global.sequence.retryTimes:5}") int retryTimes
			, @Value("${global.sequence.segmentCount:1}") int segmentCount
			, @Value("${global.sequence.maxSkipCount:5}") int maxSkipCount
			, @Value("${global.sequence.maxWaitMillis:5000}") long maxWaitMillis
			, @Value("${global.sequence.ruleClassName:}") String ruleClassName
			, @Value("${global.sequence.tableName:sequence}") String tableName) {
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

	@ConditionalOnMissingBean(name = "packagesToScan")
	@Bean(name = "packagesToScan")
	public String[] packagesToScan(@Value("${hibernate.scan.packages:}") String packages) {
	    if (packages == null || "".equals(packages)) {
	        return new String[0];
        }
		return packages.split(",");
	}

	@ConditionalOnMissingBean(name = "sequenceBeanHolder")
	@Bean(name = "sequenceBeanHolder")
	@ConfigurationProperties(prefix = "spring", ignoreUnknownFields = true)
	public SequenceBeanHolder sequenceBeanHolder() {
		return new SequenceBeanHolder() {
			private Map<String, String> beanSeqNameMap = new HashMap<String, String>();

			@Override
			public Map<String, String> getBeanSeqNameMap() {
				return beanSeqNameMap;
			}
		};
	}

	@ConditionalOnMissingBean(name = "sequenceBeanFactory")
	@Bean(name = "sequenceBeanFactory")
	public SequenceBeanFactory sequenceBeanFactory() {
		return new SequenceBeanFactory(DefaultSequence.class, "sequenceBeanHolder");
	}
}
