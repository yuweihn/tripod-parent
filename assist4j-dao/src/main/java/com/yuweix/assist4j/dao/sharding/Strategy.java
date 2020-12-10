package com.yuweix.assist4j.dao.sharding;



/**
 * 分片策略
 * @author yuwei
 */
public interface Strategy {
    /**
     * 返回分片。如：0000,0001等等
     * @param val                分片字段的值
     * @param suffixLength
     * @param shardingSize       分片总数
     * @return
     */
    String getShardingIndex(Object val, int suffixLength, int shardingSize);
}
