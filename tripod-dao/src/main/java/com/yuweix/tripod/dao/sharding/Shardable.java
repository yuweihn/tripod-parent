package com.yuweix.tripod.dao.sharding;




/**
 * @author yuwei
 */
public interface Shardable {
    Class<?> getPersistClz();
    default Object before() {
        return null;
    }
    default Object after() {
        return null;
    }
}
