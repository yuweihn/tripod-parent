package com.yuweix.tripod.dao.sharding;



/**
 * 分库/表策略
 * @author yuwei
 */
public interface Strategy {
    default ShardSetting getShardSetting(String logicName) {
        return ShardingContext.getInstance().getShardSetting(logicName);
    }

    /**
     * @param logicName                  逻辑库/表名
     * @param shardingVal                分库/表字段的值
     * @return   返回如：0000,0001等等
     */
    <T>String getShardingIndex(String logicName, T shardingVal);
}
