package com.yuweix.tripod.dao.sharding;




/**
 * @author yuwei
 */
public interface Shardable {
    Class<?> getPersistClz();
    default void before() {

    }
    default void after() {

    }
}
