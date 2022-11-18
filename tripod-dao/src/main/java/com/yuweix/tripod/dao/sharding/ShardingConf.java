package com.yuweix.tripod.dao.sharding;


import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

	@ConditionalOnMissingBean(ShardingTableHolder.class)
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
		Strategy.TABLE_CONF_MAP.clear();
		if (map != null) {
			Strategy.TABLE_CONF_MAP.putAll(map);
		}
		return map;
	}
}
