package com.yuweix.tripod.dao.hibernate;


import com.yuweix.tripod.dao.sharding.Shardable;
import java.io.Serializable;


/**
 * @author yuwei
 */
public interface Dao<T extends Serializable, PK extends Serializable> extends Shardable {
	T get(PK id);
	T get(PK id, Object shardingVal);
	void save(T t);
	void update(T t);
	void deleteByKey(PK id);
	void deleteByKey(PK id, Object shardingVal);
	void delete(T t);
}
