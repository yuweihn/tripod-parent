package com.yuweix.tripod.dao.springboot;


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
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;


/**
 * @author yuwei
 */
@EnableTransactionManagement(proxyTargetClass = true)
public class HibernateConf {
	@jakarta.annotation.Resource
	private ApplicationContext applicationContext;


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

	@Bean(name = "sessionFactory")
	public LocalSessionFactoryBean localSessionFactoryBean(@Qualifier("dataSource") DataSource dataSource
			, @Qualifier("mappingLocations") Resource[] mappingLocations
			, @Qualifier("packagesToScan") String[] packagesToScan
			, Interceptor interceptor
			, @Value("${tripod.hibernate.dialect:org.hibernate.dialect.MySQLDialect}") String dialect
			, @Value("${tripod.hibernate.current-session-context-class:org.springframework.orm.hibernate5.SpringSessionContext}") String sessionContext
			, @Value("${tripod.hibernate.cache.region.factory-class:org.hibernate.cache.ehcache.EhCacheRegionFactory}") String cacheRegionFactory
			, @Value("${tripod.hibernate.cache.use-query-cache:true}") String useQueryCache
			, @Value("${tripod.hibernate.cache.use-second-level-cache:false}") String useSecondLevelCache
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
		properties.setProperty("hibernate.cache.use_query_cache", useQueryCache);
		properties.setProperty("hibernate.cache.use_second_level_cache", useSecondLevelCache);
		properties.setProperty("hibernate.show_sql", showSql);
		properties.setProperty("hibernate.jdbc.batch_size", batchSize);
		properties.setProperty("hibernate.connection.release_mode", releaseMode);
		bean.setHibernateProperties(properties);
		bean.setMappingLocations(mappingLocations);
		bean.setPackagesToScan(packagesToScan);
		bean.setEntityInterceptor(interceptor);
		return bean;
	}

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
