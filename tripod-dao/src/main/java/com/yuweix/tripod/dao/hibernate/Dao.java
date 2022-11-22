package com.yuweix.tripod.dao.hibernate;


import java.io.Serializable;


/**
 * @author yuwei
 */
public interface Dao<T extends Serializable, PK extends Serializable> {
	T get(PK id);
	T get(PK id, Object shardingVal);
	void save(T entity);
	void update(T entity);
	void deleteByKey(PK id);
	void deleteByKey(PK id, Object shardingVal);
	void delete(T entity);
}
