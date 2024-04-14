package com.yuweix.tripod.dao.hibernate;


import com.yuweix.tripod.sharding.Shardable;
import java.io.Serializable;


/**
 * @author yuwei
 */
public interface Dao<T extends Serializable, PK extends Serializable> extends Shardable {
	T get(PK id);
	T get(Object shardingVal, PK id);
	void save(T t);
	void update(T t);
	void deleteByKey(PK id);
	void deleteByKey(Object shardingVal, PK id);
	void delete(T t);
}
