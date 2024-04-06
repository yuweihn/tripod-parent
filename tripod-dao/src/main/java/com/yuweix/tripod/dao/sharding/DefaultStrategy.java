package com.yuweix.tripod.dao.sharding;



/**
 * @author yuwei
 */
public class DefaultStrategy implements Strategy {
    @Override
    public <T>String getShardingDatabaseIndex(String databaseName, String tableName, T shardingVal) {
        DatabaseSetting dsetting = ShardingContext.getShardDatabaseSetting(databaseName);
        if (dsetting == null) {
            throw new RuntimeException("[" + databaseName + "]'s sharding-conf is required.");
        }
        TableSetting tsetting = ShardingContext.getShardSetting(tableName);
        if (tsetting == null) {
            throw new RuntimeException("[" + tableName + "]'s sharding-conf is required.");
        }
        return String.format("%0" + dsetting.getSuffixLength() + "d", hash(shardingVal) % tsetting.getDatabaseSize());
    }

    @Override
    public <T>String getShardingIndex(String tableName, T shardingVal) {
        TableSetting setting = ShardingContext.getShardSetting(tableName);
        if (setting == null) {
            throw new RuntimeException("[" + tableName + "]'s sharding-conf is required.");
        }
        return String.format("%0" + setting.getSuffixLength() + "d", hash(shardingVal) % setting.getTableSize());
    }

    private int hash(Object str) {
        if (str == null) {
            return 0;
        }
        return Math.abs(str.hashCode());
    }
}
