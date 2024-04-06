package com.yuweix.tripod.dao.sharding;



/**
 * @author yuwei
 */
public interface Shardable {
    String getLogicTableName();
    Strategy getShardingStrategy();
    default void beforeSharding(Object shardingVal) {

    }
    default void afterSharding() {

    }
}
