package com.yuweix.tripod.dao.springboot;


import com.yuweix.tripod.dao.sharding.ShardSetting;
import com.yuweix.tripod.dao.sharding.ShardingContext;
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
		Map<String, ? extends ShardSetting> getDatabases();
		Map<String, ? extends ShardSetting> getTables();
	}
	private static class Setting implements ShardSetting {
		private int suffixLength = 4;
		private int shardingSize = 2;

		@Override
		public int getSuffixLength() {
			return suffixLength;
		}

		public void setSuffixLength(int suffixLength) {
			this.suffixLength = suffixLength;
		}

		@Override
		public int getShardingSize() {
			return shardingSize;
		}

		public void setShardingSize(int shardingSize) {
			this.shardingSize = shardingSize;
		}
	}

	@Bean
	@ConfigurationProperties(prefix = "tripod.sharding", ignoreUnknownFields = true)
	public H shardingSettingHolder() {
		return new H() {
			private Map<String, Setting> databaseMap = new HashMap<>();
			private Map<String, Setting> tableMap = new HashMap<>();

			@Override
			public Map<String, Setting> getDatabases() {
				return databaseMap;
			}
			@Override
			public Map<String, Setting> getTables() {
				return tableMap;
			}
		};
	}

	@ConditionalOnMissingBean(ShardingContext.class)
	@Bean(name = "shardingContext")
	public ShardingContext shardingContext(H holder) {
		Map<String, ? extends ShardSetting> databases = holder.getDatabases();
		Map<String, ? extends ShardSetting> tables = holder.getTables();
		ShardingContext shardingContext = ShardingContext.getInstance();
		shardingContext.putDatabaseSetting(databases);
		shardingContext.putTableSetting(tables);
		return shardingContext;
	}
}
