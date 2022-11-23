package com.yuweix.tripod.boot.datasource;


import com.alibaba.druid.pool.DruidDataSource;
import com.yuweix.tripod.dao.hibernate.ShardAspect;
import com.yuweix.tripod.data.springboot.HibernateConf;
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
	@ConditionalOnMissingBean(ShardAspect.class)
	@Bean(name = "shardAspect")
	public ShardAspect shardAspect() {
		return new ShardAspect();
	}

	@ConditionalOnMissingBean(name = "dataSource")
	@Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
	public DataSource druidDataSourceMaster(@Value("${tripod.jdbc.driver.class}") String driverClassName
			, @Value("${tripod.jdbc.url}") String url
			, @Value("${tripod.jdbc.userName}") String userName
			, @Value("${tripod.jdbc.password}") String password
			, @Value("${tripod.jdbc.default.read.only:false}") boolean defaultReadOnly
			, @Value("${tripod.jdbc.filters:stat}") String filters
			, @Value("${tripod.jdbc.max.active:2}") int maxActive
			, @Value("${tripod.jdbc.initial.size:1}") int initialSize
			, @Value("${tripod.jdbc.max.wait:60000}") long maxWaitMillis
			, @Value("${tripod.jdbc.remove.abandoned:true}") boolean removeAbandoned
			, @Value("${tripod.jdbc.remove.abandoned.timeout:1800}") int removeAbandonedTimeout
			, @Value("${tripod.jdbc.min.idle:1}") int minIdle
			, @Value("${tripod.jdbc.time.between.eviction.runs.millis:60000}") long timeBetweenEvictionRunsMillis
			, @Value("${tripod.jdbc.min.evictable.idle.time.millis:300000}") long minEvictableIdleTimeMillis
			, @Value("${tripod.jdbc.validation.query:select 'x'}") String validationQuery
			, @Value("${tripod.jdbc.test.while.idle:true}") boolean testWhileIdle
			, @Value("${tripod.jdbc.test.on.borrow:false}") boolean testOnBorrow
			, @Value("${tripod.jdbc.test.on.return:false}") boolean testOnReturn
			, @Value("${tripod.jdbc.pool.prepared.statements:true}") boolean poolPreparedStatements
			, @Value("${tripod.jdbc.max.pool.prepared.statement.per.connection.size:50}") int maxPoolPreparedStatementPerConnectionSize
			, @Value("${tripod.jdbc.max.open.prepared.statements:100}") int maxOpenPreparedStatements) throws SQLException {
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
