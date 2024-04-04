package com.yuweix.tripod.dao.sharding;


import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class ShardingContext {
	private static final ShardingContext INSTANCE = new ShardingContext();
	private final Map<String, ShardSetting> dbSetting = new HashMap<>();
	private final Map<String, ShardSetting> tableSetting = new HashMap<>();

	private ShardingContext() {

	}
	public static ShardingContext getInstance() {
		return INSTANCE;
	}

	public void putDatabaseSetting(Map<String, ? extends ShardSetting> map) {
		if (map == null) {
			return;
		}
		dbSetting.putAll(map);
	}
	public void putTableSetting(Map<String, ? extends ShardSetting> map) {
		if (map == null) {
			return;
		}
		tableSetting.putAll(map);
	}

	/**
	 * 根据逻辑库名获取配置
	 */
	public ShardSetting getShardDatabaseSetting(String dbName) {
		return dbSetting.get(dbName);
	}

	/**
	 * 根据逻辑表名获取配置
	 */
	public ShardSetting getShardSetting(String tableName) {
		return tableSetting.get(tableName);
	}
}
