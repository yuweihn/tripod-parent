package com.yuweix.tripod.sharding.springboot;


import com.yuweix.tripod.dao.sharding.*;
import com.yuweix.tripod.sharding.aspect.DynamicTableAspect;
import com.yuweix.tripod.sharding.aspect.TableAspect;
import com.yuweix.tripod.sharding.context.DatabaseSetting;
import com.yuweix.tripod.sharding.context.ShardingContext;
import com.yuweix.tripod.sharding.context.TableSetting;
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
		Map<String, ? extends DatabaseSetting> getDatabases();
		Map<String, ? extends TableSetting> getTables();
	}
	private static class DSetting implements DatabaseSetting {
		private int suffixLength = 4;
		private String split = "_";

		@Override
		public int getSuffixLength() {
			return suffixLength;
		}
		public void setSuffixLength(int suffixLength) {
			this.suffixLength = suffixLength;
		}
		@Override
		public String getSplit() {
			return split;
		}
		public void setSplit(String split) {
			this.split = split;
		}
	}
	private static class TSetting implements TableSetting {
		private int suffixLength = 4;
		private String split = "_";
		private int databaseSize = 1;
		private int tableSize = 2;

		@Override
		public int getSuffixLength() {
			return suffixLength;
		}
		public void setSuffixLength(int suffixLength) {
			this.suffixLength = suffixLength;
		}
		@Override
		public String getSplit() {
			return split;
		}
		public void setSplit(String split) {
			this.split = split;
		}

		/**
		 * 如：2表示1库2表；2,4表示2库4表
		 */
		public void setShardingSize(String shardingSize) {
			String[] arr = shardingSize.split(",");
			if (arr.length == 1) {
				this.databaseSize = 1;
				this.tableSize = Integer.parseInt(arr[0].trim());
			} else {
				this.databaseSize = Integer.parseInt(arr[0].trim());
				this.tableSize = Integer.parseInt(arr[1].trim());
			}
		}

		@Override
		public int getDatabaseSize() {
			return databaseSize;
		}
		@Override
		public int getTableSize() {
			return tableSize;
		}
	}

	@Bean
	@ConfigurationProperties(prefix = "tripod.sharding", ignoreUnknownFields = true)
	public H shardingSettingHolder() {
		return new H() {
			private Map<String, DSetting> databaseMap = new HashMap<>();
			private Map<String, TSetting> tableMap = new HashMap<>();

			@Override
			public Map<String, DSetting> getDatabases() {
				return databaseMap;
			}
			@Override
			public Map<String, TSetting> getTables() {
				return tableMap;
			}
		};
	}

	@ConditionalOnMissingBean(name = "shardingContext")
	@Bean(name = "shardingContext")
	public ShardingContext shardingContext(H holder) {
		Map<String, ? extends DatabaseSetting> databases = holder.getDatabases();
		Map<String, ? extends TableSetting> tables = holder.getTables();
		ShardingContext.putDatabaseSetting(databases);
		ShardingContext.putTableSetting(tables);
		return null;
	}

	@ConditionalOnMissingBean(TableAspect.class)
	@Bean(name = "dynamicTableAspect")
	public DynamicTableAspect dynamicTableAspect() {
		return new DynamicTableAspect();
	}
}
