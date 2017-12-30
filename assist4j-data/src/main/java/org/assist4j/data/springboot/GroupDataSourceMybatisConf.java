package org.assist4j.data.springboot;


import org.apache.ibatis.session.SqlSessionFactory;
import org.assist4j.data.ds.DataSourceCluster;
import org.assist4j.data.ds.DataSourceAspect;
import org.assist4j.data.ds.DynamicDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.List;


/**
 * @author yuwei
 */
@EnableTransactionManagement(proxyTargetClass = true)
public class GroupDataSourceMybatisConf {

	@Bean(name = "dataSource")
	public DataSource dynamicDataSource(@Qualifier("dsClusterList") List<DataSourceCluster> dsClusterList) {
		return new DynamicDataSource(dsClusterList);
	}

	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactoryBean sqlSessionFactoryBean(@Qualifier("dataSource") DataSource dataSource
			, @Qualifier("mapperLocations") Resource[] mapperLocations) {
		SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
		sessionFactoryBean.setDataSource(dataSource);
		sessionFactoryBean.setMapperLocations(mapperLocations);
		return sessionFactoryBean;
	}

	@Bean(name = "sqlSessionTemplate")
	public SqlSessionTemplate sqlSessionTemplate(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	@Bean
	public MapperScannerConfigurer mapperScannerConfigurer(@Qualifier("basePackage") String basePackage) {
		MapperScannerConfigurer configurer = new MapperScannerConfigurer();
		configurer.setSqlSessionTemplateBeanName("sqlSessionTemplate");
		configurer.setBasePackage(basePackage);
		return configurer;
	}

	@Bean(name = "transactionManager")
	public DataSourceTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource);
		return transactionManager;
	}

	@Bean(name = "dataSourceAspect")
	public DataSourceAspect dataSourceAdvice(@Qualifier("dsClusterList") List<DataSourceCluster> dsClusterList) {
		return new DataSourceAspect(dsClusterList);
	}
}
