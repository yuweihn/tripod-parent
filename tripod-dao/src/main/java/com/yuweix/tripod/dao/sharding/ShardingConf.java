package com.yuweix.tripod.dao.sharding;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class ShardingConf {
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

	@Bean(name = "ShardingConf$tableConf")
	public Map<String, TableConfig> initTableConfMap(ShardingTableHolder shardingTableHolder) {
		Map<String, TableConfig> map = shardingTableHolder.getTables();

		Strategy.TABLE_CONF_MAP.clear();
		if (map != null) {
			Strategy.TABLE_CONF_MAP.putAll(map);
		}
		return map;
	}
}
