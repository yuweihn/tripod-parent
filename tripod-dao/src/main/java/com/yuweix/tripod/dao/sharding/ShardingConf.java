package com.yuweix.tripod.dao.sharding;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class ShardingConf {
	interface H {
		Map<String, TableConfig> getTables();
	}

	@Bean
	@ConfigurationProperties(prefix = "tripod.sharding", ignoreUnknownFields = true)
	public H shardingTableHolder() {
		return new H() {
			private Map<String, TableConfig> map = new HashMap<>();

			@Override
			public Map<String, TableConfig> getTables() {
				return map;
			}
		};
	}

	@Bean(name = "shardingContext")
	public ShardingContext shardingContext(H shardingTableHolder) {
		ShardingContext shardingContext = new ShardingContext();
		Map<String, TableConfig> map = shardingTableHolder.getTables();
		Context.initTableConf(map);
		shardingContext.initTableConf(map);
		return shardingContext;
	}
}
