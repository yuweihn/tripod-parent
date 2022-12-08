package com.yuweix.tripod.dao.springboot;


import com.alibaba.druid.pool.DruidDataSource;
import com.yuweix.tripod.dao.hibernate.DynamicTableInterceptor;
import com.yuweix.tripod.dao.hibernate.ShardAspect;
import com.yuweix.tripod.dao.sharding.ShardingContext;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Properties;


/**
 * @author yuwei
 */
@EnableTransactionManagement(proxyTargetClass = true)
public class HibernateConf {
	@javax.annotation.Resource
	private ApplicationContext applicationContext;


	@ConditionalOnMissingBean(name = "dataSource")
	@Bean(name = "dataSource", initMethod = "init", destroyMethod = "close")
	public DataSource defaultDataSource(@Value("${tripod.datasource.default.driver-class}") String driverClassName
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

	@ConditionalOnMissingBean(name = "mappingLocations")
	@Bean(name = "mappingLocations")
	public Resource[] mappingLocations() {
		return new Resource[] {};
	}

	@ConditionalOnMissingBean(name = "packagesToScan")
	@Bean(name = "packagesToScan")
	public String[] packagesToScan(@Value("${tripod.hibernate.scan.packages:}") String packages) {
		if (packages == null || "".equals(packages)) {
			return new String[0];
		}
		return packages.split(",");
	}

	@ConditionalOnMissingBean(SessionFactory.class)
	@Bean(name = "sessionFactory")
	public LocalSessionFactoryBean localSessionFactoryBean(@Qualifier("dataSource") DataSource dataSource
			, @Qualifier("mappingLocations") Resource[] mappingLocations
			, @Qualifier("packagesToScan") String[] packagesToScan
			, Interceptor interceptor
			, @Value("${tripod.hibernate.dialect:org.hibernate.dialect.MySQLDialect}") String dialect
			, @Value("${tripod.hibernate.current-session-context-class:org.springframework.orm.hibernate5.SpringSessionContext}") String sessionContext
			, @Value("${tripod.hibernate.cache.region.factory-class:org.hibernate.cache.jcache.internal.JCacheRegionFactory}") String cacheRegionFactory
			, @Value("${tripod.hibernate.cache.provider-class:org.ehcache.jsr107.EhcacheCachingProvider}") String cacheProviderClass
			, @Value("${tripod.hibernate.cache.provider-configuration-file-resource-path:ehcache-default.xml}") String ehcachePath
			, @Value("${tripod.hibernate.cache.use-query-cache:true}") String useQueryCache
			, @Value("${tripod.hibernate.cache.use-second-level-cache:false}") String useSecondLevelCache
			, @Value("${tripod.hibernate.cache.missing-cache-strategy:create}") String missingCacheStrategy
			, @Value("${tripod.hibernate.show-sql:false}") String showSql
			, @Value("${tripod.hibernate.jdbc.batch-size:20}") String batchSize
			, @Value("${tripod.hibernate.connection.release-mode:auto}") String releaseMode) {
		/**
		 * 如果有分片上下文配置，优先加载
		 */
		try {
			applicationContext.getBean(ShardingContext.class);
		} catch (Exception ignored) {}

		LocalSessionFactoryBean bean = new LocalSessionFactoryBean();
		bean.setDataSource(dataSource);

		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", dialect);
		properties.setProperty("hibernate.current_session_context_class", sessionContext);
		properties.setProperty("hibernate.cache.region.factory_class", cacheRegionFactory);
		properties.setProperty("hibernate.cache.provider_class", cacheProviderClass);
		properties.setProperty("hibernate.cache.provider_configuration_file_resource_path", ehcachePath);
		properties.setProperty("hibernate.cache.use_query_cache", useQueryCache);
		properties.setProperty("hibernate.cache.use_second_level_cache", useSecondLevelCache);
		properties.setProperty("hibernate.javax.cache.missing_cache_strategy", missingCacheStrategy);
		properties.setProperty("hibernate.show_sql", showSql);
		properties.setProperty("hibernate.jdbc.batch_size", batchSize);
		properties.setProperty("hibernate.connection.release_mode", releaseMode);
		bean.setHibernateProperties(properties);
		bean.setMappingLocations(mappingLocations);
		bean.setPackagesToScan(packagesToScan);
		bean.setEntityInterceptor(interceptor);
		return bean;
	}

	@ConditionalOnMissingBean(TransactionManager.class)
	@Bean(name = "transactionManager")
	public HibernateTransactionManager transactionManager(@Qualifier("sessionFactory") SessionFactory sessionFactory) {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(sessionFactory);
		return transactionManager;
	}

	@ConditionalOnMissingBean(ShardAspect.class)
	@Bean(name = "hbShardAspect")
	public ShardAspect hbShardAspect() {
		return new ShardAspect();
	}

	@ConditionalOnMissingBean(Interceptor.class)
	@Bean(name = "dynamicTableInterceptor")
	public Interceptor dynamicTableInterceptor() {
		return new DynamicTableInterceptor();
	}
}
