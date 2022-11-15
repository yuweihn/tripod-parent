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
	@ConditionalOnMissingBean(name = "shardingTableHolder")
	@Bean(name = "shardingTableHolder")
	@ConfigurationProperties(prefix = "tripod", ignoreUnknownFields = true)
	public ShardingTableHolder shardingTableHolder() {
		return new ShardingTableHolder() {
			private Map<String, Config> conf = new HashMap<String, Config>();

			@Override
			public Map<String, Config> getShardingConf() {
				return conf;
			}
		};
	}

	@Bean(name = "ShardingConf$tableConf")
	public Map<String, Config> initTableConfMap(@Qualifier("shardingTableHolder") ShardingTableHolder shardingTableHolder) {
		Map<String, Config> conf = shardingTableHolder.getShardingConf();

		Strategy.TABLE_CONF_MAP.clear();
		if (conf != null) {
			Strategy.TABLE_CONF_MAP.putAll(conf);
		}
		return conf;
	}
}
