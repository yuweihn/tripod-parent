package com.yuweix.assist4j.boot.datasource;


import com.alibaba.druid.pool.DruidDataSource;
import com.yuweix.assist4j.data.springboot.MybatisConf;
import com.yuweix.assist4j.sequence.springboot.SequenceConf;
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
@ConditionalOnProperty(name = "assist4j.boot.mybatis.enabled")
@Import({MybatisConf.class, SequenceConf.class})
public class MybatisAutoConfiguration {
	@ConditionalOnMissingBean(name = "dataSource")
	@Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
	public DataSource druidDataSourceMaster(@Value("${jdbc.driver.class}") String driverClassName
			, @Value("${jdbc.url}") String url
			, @Value("${jdbc.userName}") String userName
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
}
