package com.yuweix.tripod.dao.sharding;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class ShardingConf {
	interface TableHolder {
		Map<String, TableConfig> getTables();
	}

	@Bean
	@ConfigurationProperties(prefix = "tripod.sharding", ignoreUnknownFields = true)
	public TableHolder tableHolder() {
		return new TableHolder() {
			private Map<String, TableConfig> map = new HashMap<>();

			@Override
			public Map<String, TableConfig> getTables() {
				return map;
			}
		};
	}

	@Bean(name = "shardingTableConf")
	public Map<String, TableConfig> shardingTableConf(TableHolder tableHolder) {
		Map<String, TableConfig> map = tableHolder.getTables();
		Constant.initTableConf(map);
		return map;
	}
}
