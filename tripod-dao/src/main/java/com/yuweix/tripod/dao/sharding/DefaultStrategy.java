package com.yuweix.tripod.dao.sharding;



/**
 * @author yuwei
 */
public class DefaultStrategy implements Strategy {
    @Override
    public <T> String getShardingDatabaseIndex(String logicName, T shardingVal) {
        ShardSetting setting = ShardingContext.getInstance().getShardDatabaseSetting(logicName);
        if (setting == null) {
            throw new RuntimeException("[" + logicName + "]'s sharding-conf is required.");
        }
        return String.format("%0" + setting.getSuffixLength() + "d", hash(shardingVal) % setting.getShardingSize());
    }

    @Override
    public <T>String getShardingIndex(String logicName, T shardingVal) {
        ShardSetting setting = ShardingContext.getInstance().getShardSetting(logicName);
        if (setting == null) {
            throw new RuntimeException("[" + logicName + "]'s sharding-conf is required.");
        }
        return String.format("%0" + setting.getSuffixLength() + "d", hash(shardingVal) % setting.getShardingSize());
    }

    private int hash(Object str) {
        if (str == null) {
            return 0;
        }
        return Math.abs(str.hashCode());
    }
}
