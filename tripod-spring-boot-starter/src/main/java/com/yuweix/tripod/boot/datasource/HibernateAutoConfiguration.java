package com.yuweix.tripod.boot.datasource;


import com.alibaba.druid.pool.DruidDataSource;
import com.yuweix.tripod.dao.springboot.HibernateConf;
import com.yuweix.tripod.sequence.springboot.SequenceConf;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.sql.SQLException;


/**
 * @author yuwei
 */
@Configuration
@ConditionalOnProperty(name = "tripod.boot.hibernate.enabled")
@Import({HibernateConf.class, SequenceConf.class})
public class HibernateAutoConfiguration {
	@ConditionalOnMissingBean(name = "dataSource")
	@Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
	public DataSource druidDataSourceMaster(@Value("${tripod.datasource.default.driver-class}") String driverClassName
			, @Value("${tripod.datasource.default.url}") String url
			, @Value("${tripod.datasource.default.user-name}") String userName
			, @Value("${tripod.datasource.default.password}") String password
			, @Value("${tripod.datasource.default.default-read-only:false}") boolean defaultReadOnly
			, @Value("${tripod.datasource.default.filters:stat}") String filters
			, @Value("${tripod.datasource.default.max-active:2}") int maxActive
			, @Value("${tripod.datasource.default.initial-size:1}") int initialSize
			, @Value("${tripod.datasource.default.max-wait-mills:60000}") long maxWaitMillis
			, @Value("${tripod.datasource.default.remove-abandoned:false}") boolean removeAbandoned
			, @Value("${tripod.datasource.default.remove-abandoned-timeout:1800}") int removeAbandonedTimeout
			, @Value("${tripod.datasource.default.min-idle:1}") int minIdle
			, @Value("${tripod.datasource.default.time-between-eviction-runs-millis:60000}") long timeBetweenEvictionRunsMillis
			, @Value("${tripod.datasource.default.min-evictable-idle-time-millis:300000}") long minEvictableIdleTimeMillis
			, @Value("${tripod.datasource.default.validation-query:select 'x'}") String validationQuery
			, @Value("${tripod.datasource.default.test-while-idle:true}") boolean testWhileIdle
			, @Value("${tripod.datasource.default.test-on-borrow:false}") boolean testOnBorrow
			, @Value("${tripod.datasource.default.test-on-return:false}") boolean testOnReturn
			, @Value("${tripod.datasource.default.pool-prepared-statements:true}") boolean poolPreparedStatements
			, @Value("${tripod.datasource.default.max-pool-prepared-statement-per-connection-size:50}") int maxPoolPreparedStatementPerConnectionSize
			, @Value("${tripod.datasource.default.max-open-prepared-statements:100}") int maxOpenPreparedStatements) throws SQLException {
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
}
