package com.yuweix.tripod.dao.sharding;




/**
 * @author yuwei
 */
public interface Shardable {
    Class<?> getPersistClz();
    default void before(Object obj) {

    }
    default void after() {

    }
}
