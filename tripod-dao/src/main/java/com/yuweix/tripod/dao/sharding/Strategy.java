package com.yuweix.tripod.dao.sharding;


import java.util.HashMap;
import java.util.Map;


/**
 * 分片策略
 * @author yuwei
 */
public interface Strategy {
    Map<String, TableConfig> TABLE_CONF_MAP = new HashMap<>();

    default TableConfig getTableConf(String tableName) {
        return TABLE_CONF_MAP.get(tableName);
    }

    /**
     * @param shardingVal                分片字段的值
     * @return   返回分片。如：0000,0001等等
     */
    String getShardingIndex(String tableName, Object shardingVal);
}
