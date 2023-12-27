package com.yuweix.tripod.dao.sharding;


import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class ShardingContext {
	private static final ShardingContext INSTANCE = new ShardingContext();
	private final Map<String, TableConfig> tableConfMap = new HashMap<>();

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

	public TableConfig getTableConf(String tableName) {
		return tableConfMap.get(tableName);
	}

	public Map<String, TableConfig> getTableConfMap() {
		return tableConfMap;
	}
}
