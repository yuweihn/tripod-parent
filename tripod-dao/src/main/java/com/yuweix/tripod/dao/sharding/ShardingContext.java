package com.yuweix.tripod.dao.sharding;


import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class ShardingContext {
	private static final ShardingContext INSTANCE = new ShardingContext();
	private final Map<String, TableConfig> tableConfMap = new HashMap<>();
	private final Map<String, DatabaseConfig> databaseConfMap = new HashMap<>();

	private ShardingContext() {

	}
	public static ShardingContext getInstance() {
		return INSTANCE;
	}

	public void initTableConf(Map<String, TableConfig> map) {
		tableConfMap.clear();
		if (map != null) {
			tableConfMap.putAll(map);
		}
	}

	public void initDatabaseConf(Map<String, DatabaseConfig> map) {
		databaseConfMap.clear();
		if (map != null) {
			databaseConfMap.putAll(map);
		}
	}

	public TableConfig getTableConf(String tableName) {
		return tableConfMap.get(tableName);
	}

	public Map<String, TableConfig> getTableConfMap() {
		return tableConfMap;
	}

	public DatabaseConfig getDatabaseConf(String tableName) {
		return databaseConfMap.get(tableName);
	}

	public Map<String, DatabaseConfig> getDatabaseConfMap() {
		return databaseConfMap;
	}
}
