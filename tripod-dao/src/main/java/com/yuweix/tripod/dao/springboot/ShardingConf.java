package com.yuweix.tripod.dao.springboot;


import com.yuweix.tripod.dao.sharding.DatabaseConfig;
import com.yuweix.tripod.dao.sharding.ShardingContext;
import com.yuweix.tripod.dao.sharding.TableConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class ShardingConf {
	interface H {
		Map<String, DatabaseConfig> getDatabases();
		Map<String, TableConfig> getTables();
	}

	@Bean
	@ConfigurationProperties(prefix = "tripod.sharding", ignoreUnknownFields = true)
	public H shardingTableHolder() {
		return new H() {
			private Map<String, DatabaseConfig> databaseMap = new HashMap<>();
			private Map<String, TableConfig> tableMap = new HashMap<>();

			@Override
			public Map<String, DatabaseConfig> getDatabases() {
				return databaseMap;
			}
			@Override
			public Map<String, TableConfig> getTables() {
				return tableMap;
			}
		};
	}

	@ConditionalOnMissingBean(ShardingContext.class)
	@Bean(name = "shardingContext")
	public ShardingContext shardingContext(H holder) {
		Map<String, DatabaseConfig> databases = holder.getDatabases();
		Map<String, TableConfig> tables = holder.getTables();
		ShardingContext shardingContext = ShardingContext.getInstance();
		shardingContext.initDatabaseConf(databases);
		shardingContext.initTableConf(tables);
		return shardingContext;
	}
}
