package com.yuweix.tripod.sharding;




/**
 * @author yuwei
 */
public interface Shardable {
    Class<?> getPersistClz();
    default void onStart() {

    }
    default void onSuccess() {

    }
    default void onFailure() {

    }
    default void onComplete() {

    }
}
