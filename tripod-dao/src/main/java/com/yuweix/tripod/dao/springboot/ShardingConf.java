package com.yuweix.tripod.dao.springboot;


import com.yuweix.tripod.dao.sharding.ShardAspect;
import com.yuweix.tripod.dao.sharding.TableSetting;
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
		Map<String, ? extends TableSetting> getDatabases();
		Map<String, ? extends TableSetting> getTables();
	}
	private static class Setting implements TableSetting {
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
		Map<String, ? extends TableSetting> databases = holder.getDatabases();
		Map<String, ? extends TableSetting> tables = holder.getTables();
		ShardingContext.putDatabaseSetting(databases);
		ShardingContext.putTableSetting(tables);
		return null;
	}

	@ConditionalOnMissingBean(ShardAspect.class)
	@Bean(name = "shardAspect")
	public ShardAspect hbShardAspect() {
		return new ShardAspect();
	}
}
