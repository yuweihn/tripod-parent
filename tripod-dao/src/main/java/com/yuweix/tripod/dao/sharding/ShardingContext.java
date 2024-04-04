package com.yuweix.tripod.dao.sharding;


import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public final class ShardingContext {
	private static final Map<String, ShardSetting> DB_SETTING = new HashMap<>();
	private static final Map<String, ShardSetting> TABLE_SETTING = new HashMap<>();

	private ShardingContext() {

	}

	public static void putDatabaseSetting(Map<String, ? extends ShardSetting> map) {
		if (map == null) {
			return;
		}
		DB_SETTING.putAll(map);
	}
	public static void putTableSetting(Map<String, ? extends ShardSetting> map) {
		if (map == null) {
			return;
		}
		TABLE_SETTING.putAll(map);
	}

	/**
	 * 根据逻辑库名获取配置
	 */
	public static ShardSetting getShardDatabaseSetting(String dbName) {
		return DB_SETTING.get(dbName);
	}

	/**
	 * 根据逻辑表名获取配置
	 */
	public static ShardSetting getShardSetting(String tableName) {
		return TABLE_SETTING.get(tableName);
	}
}
