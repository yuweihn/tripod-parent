package com.yuweix.tripod.sharding.strategy;


import com.yuweix.tripod.sharding.context.DatabaseSetting;
import com.yuweix.tripod.sharding.context.ShardingContext;
import com.yuweix.tripod.sharding.context.TableSetting;


/**
 * @author yuwei
 */
public class DefaultStrategy implements Strategy {
    @Override
    public <T>String getPhysicalDatabaseName(String dbName, String tableName, T shardingVal) {
        DatabaseSetting dsetting = ShardingContext.getShardDatabaseSetting(dbName);
        if (dsetting == null) {
            throw new RuntimeException("[" + dbName + "]'s sharding-conf is required.");
        }
        TableSetting tsetting = ShardingContext.getShardSetting(tableName);
        if (tsetting == null) {
            throw new RuntimeException("[" + tableName + "]'s sharding-conf is required.");
        }
        return dbName + dsetting.getSplit() + String.format("%0" + dsetting.getSuffixLength() + "d", hash(shardingVal) % tsetting.getDatabaseSize());
    }

    @Override
    public <T>String getPhysicalTableName(String tableName, T shardingVal) {
        TableSetting setting = ShardingContext.getShardSetting(tableName);
        if (setting == null) {
            throw new RuntimeException("[" + tableName + "]'s sharding-conf is required.");
        }
        return tableName + setting.getSplit() + String.format("%0" + setting.getSuffixLength() + "d", (hash(shardingVal) / setting.getDatabaseSize()) % setting.getTableSize());
    }

    private int hash(Object str) {
        if (str == null) {
            return 0;
        }
        return Math.abs(str.hashCode());
    }
}
