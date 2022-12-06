package com.yuweix.tripod.boot.datasource;


import com.alibaba.druid.pool.DruidDataSource;
import com.yuweix.tripod.dao.PersistCache;
import com.yuweix.tripod.data.cache.Cache;
import com.yuweix.tripod.dao.springboot.MybatisConf;
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
@ConditionalOnProperty(name = "tripod.boot.mybatis.enabled")
@Import({MybatisConf.class, SequenceConf.class})
public class MybatisAutoConfiguration {
	@ConditionalOnMissingBean(PersistCache.class)
	@Bean(name = "persistCache")
	public PersistCache persistCache(Cache cache) {
		return new PersistCache() {
			@Override
			public <T> boolean put(String key, T t, long timeout) {
				return cache.put(key, t, timeout);
			}
			@Override
			public <T> T get(String key) {
				return cache.get(key);
			}
			@Override
			public void remove(String key) {
				cache.remove(key);
			}
		};
	}

	@ConditionalOnProperty(name = "tripod.datasource.enabled")
	@Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
	public DataSource druidDataSourceMaster(@Value("${tripod.datasource.driver-class}") String driverClassName
			, @Value("${tripod.datasource.url}") String url
			, @Value("${tripod.datasource.user-name}") String userName
			, @Value("${tripod.datasource.password}") String password
			, @Value("${tripod.datasource.default-read-only:false}") boolean defaultReadOnly
			, @Value("${tripod.datasource.filters:stat}") String filters
			, @Value("${tripod.datasource.max-active:2}") int maxActive
			, @Value("${tripod.datasource.initial-size:1}") int initialSize
			, @Value("${tripod.datasource.max-wait-mills:60000}") long maxWaitMillis
			, @Value("${tripod.datasource.remove-abandoned:false}") boolean removeAbandoned
			, @Value("${tripod.datasource.remove-abandoned-timeout:1800}") int removeAbandonedTimeout
			, @Value("${tripod.datasource.min-idle:1}") int minIdle
			, @Value("${tripod.datasource.time-between-eviction-runs-millis:60000}") long timeBetweenEvictionRunsMillis
			, @Value("${tripod.datasource.min-evictable-idle-time-millis:300000}") long minEvictableIdleTimeMillis
			, @Value("${tripod.datasource.validation-query:select 'x'}") String validationQuery
			, @Value("${tripod.datasource.test-while-idle:true}") boolean testWhileIdle
			, @Value("${tripod.datasource.test-on-borrow:false}") boolean testOnBorrow
			, @Value("${tripod.datasource.test-on-return:false}") boolean testOnReturn
			, @Value("${tripod.datasource.pool-prepared-statements:true}") boolean poolPreparedStatements
			, @Value("${tripod.datasource.max-pool-prepared-statement-per-connection-size:50}") int maxPoolPreparedStatementPerConnectionSize
			, @Value("${tripod.datasource.max-open-prepared-statements:100}") int maxOpenPreparedStatements) throws SQLException {
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
