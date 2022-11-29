package com.yuweix.tripod.dao.sharding;


import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class ShardingContext {
	private static final ShardingContext INSTANCE = new ShardingContext();
	private final Map<String, TableConfig> TABLE_CONF_MAP = new HashMap<>();

	private ShardingContext() {

	}
	public static ShardingContext getInstance() {
		return INSTANCE;
	}

	public void initTableConf(Map<String, TableConfig> map) {
		TABLE_CONF_MAP.clear();
		if (map != null) {
			TABLE_CONF_MAP.putAll(map);
		}
	}

	public TableConfig getTableConf(String tableName) {
		return TABLE_CONF_MAP.get(tableName);
	}

	public Map<String, TableConfig> getTableConfMap() {
		return TABLE_CONF_MAP;
	}
}
