package com.yuweix.tripod.dao.sharding;



/**
 * @author yuwei
 */
public class DefaultStrategy implements Strategy {
    @Override
    public <T>String getShardingIndex(String tableName, T shardingVal) {
        TableConfig conf = getTableConf(tableName);
        if (conf == null) {
            throw new RuntimeException("[" + tableName + "]'s sharding-conf is required.");
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
