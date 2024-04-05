package com.yuweix.tripod.dao.mybatis;


import com.yuweix.tripod.dao.PersistUtil;
import com.yuweix.tripod.dao.mybatis.order.OrderBy;
import com.yuweix.tripod.dao.mybatis.where.Criteria;
import com.yuweix.tripod.dao.sharding.Shard;
import com.yuweix.tripod.dao.sharding.Sharding;
import com.yuweix.tripod.dao.sharding.Strategy;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;


/**
 * @author yuwei
 */
public abstract class AbstractDao<T extends Serializable, PK extends Serializable> implements Dao<T, PK> {
	protected Class<T> clz;


	@SuppressWarnings("unchecked")
	public AbstractDao() {
		this.clz = null;
		Type t = getClass().getGenericSuperclass();
		if (t instanceof ParameterizedType) {
			this.clz = (Class<T>) ((ParameterizedType) t).getActualTypeArguments()[0];
		}
	}
	
	protected abstract BaseMapper<T, PK> getMapper();

	@Override
	public Strategy getShardingStrategy() {
		PersistUtil.FieldCol fieldCol = PersistUtil.getShardingFieldCol(clz);
		if (fieldCol == null) {
			return null;
		}
		if (!fieldCol.getField().isAnnotationPresent(Sharding.class)) {
			return null;
		}
		Sharding sharding = fieldCol.getField().getAnnotation(Sharding.class);
		if (sharding == null) {
			return null;
		}
		Class<? extends Strategy> strategyClz = sharding.strategy();
		if (strategyClz == null) {
			return null;
		}
		try {
			return strategyClz.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public T get(PK id) {
		return getMapper().selectOneById(id, clz);
	}

	@Override
	public T get(@Shard Object shardingVal, PK id) {
		return getMapper().selectOneByIdSharding(shardingVal, id, clz);
	}

	@Override
	public int findCount(Criteria criteria) {
		return getMapper().findCount(criteria, clz);
	}

	@Override
	public List<T> findList(Criteria criteria, OrderBy orderBy) {
		return getMapper().findList(criteria, orderBy, clz);
	}

	@Override
	public List<T> findPageList(Criteria criteria, int pageNo, int pageSize, OrderBy orderBy) {
		return getMapper().findPageList(criteria, pageNo, pageSize, orderBy, clz);
	}

	@Override
	public int insert(@Shard T t) {
		return getMapper().insert(t);
	}

	@Override
	public int insertSelective(@Shard T t) {
		return getMapper().insertSelective(t);
	}

	@Override
	public int updateByPrimaryKey(@Shard T t) {
		return getMapper().updateByPrimaryKey(t);
	}

	@Override
	public int updateByPrimaryKeyExcludeVersion(@Shard T t) {
		return getMapper().updateByPrimaryKeyExcludeVersion(t);
	}

	@Override
	public int updateByPrimaryKeySelective(@Shard T t) {
		return getMapper().updateByPrimaryKeySelective(t);
	}

	@Override
	public int updateByPrimaryKeySelectiveExcludeVersion(@Shard T t) {
		return getMapper().updateByPrimaryKeySelectiveExcludeVersion(t);
	}

	@Override
	public int delete(@Shard T t) {
		return getMapper().delete(t);
	}

	@Override
	public int deleteByKey(PK id) {
		return getMapper().deleteByKey(id, clz);
	}

	@Override
	public int deleteByKey(@Shard Object shardingVal, PK id) {
		return getMapper().deleteByKeySharding(shardingVal, id, clz);
	}
}
