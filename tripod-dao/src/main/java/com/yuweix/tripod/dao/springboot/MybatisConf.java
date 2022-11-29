package com.yuweix.tripod.dao.springboot;


import com.yuweix.tripod.dao.sharding.ShardingContext;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.io.IOException;


/**
 * @author yuwei
 */
@EnableTransactionManagement(proxyTargetClass = true)
public class MybatisConf {
	@javax.annotation.Resource
	private ApplicationContext applicationContext;

	@ConditionalOnMissingBean(name = "mapperLocations")
	@Bean(name = "mapperLocations")
	public Resource[] mapperLocations(@Value("${tripod.mybatis.mapper.location-pattern:}") String locationPattern) throws IOException {
		if (locationPattern == null || "".equals(locationPattern)) {
			return new Resource[0];
		}
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		return resolver.getResources(locationPattern);
	}

	@ConditionalOnMissingBean(name = "basePackage")
	@Bean(name = "basePackage")
	public String basePackage(Environment env) {
		return env.getProperty("tripod.mybatis.base-package");
	}

	@ConditionalOnMissingBean(name = "sqlSessionFactory")
	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactoryBean sqlSessionFactoryBean(@Qualifier("dataSource") DataSource dataSource
			, @Qualifier("mapperLocations") Resource[] mapperLocations) {
		/**
		 * 如果有分片上下文配置，优先加载
		 */
		try {
			applicationContext.getBean(ShardingContext.class);
		} catch (Exception ignored) {}

		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
		if (mapperLocations != null && mapperLocations.length > 0) {
			sessionFactoryBean.setMapperLocations(mapperLocations);
		}
		return sessionFactoryBean;
	}

	@ConditionalOnMissingBean(name = "sqlSessionTemplate")
	@Bean(name = "sqlSessionTemplate")
	public SqlSessionTemplate SqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	@ConditionalOnMissingBean(name = "mapperScannerConf")
	@Bean(name = "mapperScannerConf")
	public MapperScannerConfigurer mapperScannerConf(@Qualifier("basePackage") String basePackage) {
		MapperScannerConfigurer conf = new MapperScannerConfigurer();
//		conf.setSqlSessionFactoryBeanName("sqlSessionFactory");
		conf.setSqlSessionTemplateBeanName("sqlSessionTemplate");
		conf.setBasePackage(basePackage);
		return conf;
	}

	@ConditionalOnMissingBean(name = "transactionManager")
	@Bean(name = "transactionManager")
	public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}

	@ConditionalOnMissingBean(name = "transactionTemplate")
	@Bean(name = "transactionTemplate")
	public TransactionTemplate transactionTemplate(@Qualifier("transactionManager") PlatformTransactionManager transactionManager) {
		return new TransactionTemplate(transactionManager);
	}
}
