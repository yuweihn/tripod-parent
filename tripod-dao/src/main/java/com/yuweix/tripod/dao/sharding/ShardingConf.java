package com.yuweix.tripod.dao.sharding;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class ShardingConf {
	interface ShardingTableHolder {
		Map<String, TableConfig> getTables();
	}

	@Bean
	@ConfigurationProperties(prefix = "tripod.sharding", ignoreUnknownFields = true)
	public ShardingTableHolder shardingTableHolder() {
		return new ShardingTableHolder() {
			private Map<String, TableConfig> map = new HashMap<>();

			@Override
			public Map<String, TableConfig> getTables() {
				return map;
			}
		};
	}

	@Bean(name = "shardingTableConf")
	public Map<String, TableConfig> shardingTableConf(ShardingTableHolder shardingTableHolder) {
		Map<String, TableConfig> map = shardingTableHolder.getTables();
		Constant.putTableConf(map);
		return map;
	}
}
