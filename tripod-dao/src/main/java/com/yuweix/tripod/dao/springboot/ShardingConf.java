package com.yuweix.tripod.dao.springboot;


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

	@ConditionalOnMissingBean(ShardingContext.class)
	@Bean(name = "shardingContext")
	public ShardingContext shardingContext(H shardingTableHolder) {
		Map<String, TableConfig> map = shardingTableHolder.getTables();
		ShardingContext shardingContext = ShardingContext.getInstance();
		shardingContext.initTableConf(map);
		return shardingContext;
	}
}
