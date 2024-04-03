package com.yuweix.tripod.dao.sharding;


import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class ShardingContext {
	private static final ShardingContext INSTANCE = new ShardingContext();
	private final Map<String, ShardSetting> setting = new HashMap<>();

	private ShardingContext() {

	}
	public static ShardingContext getInstance() {
		return INSTANCE;
	}

	public void putSetting(Map<String, ? extends ShardSetting> map) {
		if (map != null) {
			setting.putAll(map);
		}
	}

	/**
	 * 根据逻辑库/表名获取配置
	 */
	public ShardSetting getShardSetting(String name) {
		return setting.get(name);
	}
}
