package com.yuweix.tripod.dao.sharding;



/**
 * 分库策略-取模
 * @author yuwei
 */
public class DatabaseModStrategy implements DatabaseStrategy {
    @Override
    public <T>String getShardingIndex(String databaseName, T shardingVal) {
        DatabaseConfig conf = getDatabaseConf(databaseName);
        if (conf == null) {
            throw new RuntimeException("[" + databaseName + "]'s sharding-conf is required.");
        }
        return String.format("%0" + conf.getSuffixLength() + "d", hash(shardingVal) % conf.getShardingSize());
    }

    private int hash(Object str) {
        if (str == null) {
            return 0;
        }
        return Math.abs(str.hashCode());
    }
}
