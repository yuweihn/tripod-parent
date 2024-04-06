package com.yuweix.tripod.dao.sharding;


import com.yuweix.tripod.dao.PersistUtil;


/**
 * @author yuwei
 */
public interface Shardable {
    Class<?> getPersistClz();
    Strategy getShardingStrategy();
    default void beforeSharding(Object shardingVal) {
        if (shardingVal == null) {
            return;
        }
        Class<?> clz = getPersistClz();
        String srcTableName = PersistUtil.getTableName(clz);
        String targetTableName = PersistUtil.getPhysicalTableName(clz, shardingVal);
        DynamicTableTL.set(srcTableName, targetTableName);
    }
    default void afterSharding() {
        DynamicTableTL.remove();
    }
}
