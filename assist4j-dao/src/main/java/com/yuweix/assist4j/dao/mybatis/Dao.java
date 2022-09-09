package com.yuweix.assist4j.dao.mybatis;


import com.yuweix.assist4j.dao.mybatis.order.OrderBy;
import com.yuweix.assist4j.dao.mybatis.where.Criteria;

import java.io.Serializable;
import java.util.List;


/**
 * @author yuwei
 */
public interface Dao<T extends Serializable, PK extends Serializable> {
	T get(PK id);
	
	int findCount(Criteria criteria);
	List<T> findList(Criteria criteria, OrderBy orderBy);
	List<T> findPageList(Criteria criteria, int pageNo, int pageSize, OrderBy orderBy);
	
	int insert(T t);
	int insertSelective(T t);
	int updateByPrimaryKey(T t);
	int updateExcludeVersionByPrimaryKey(T t);
	int updateByPrimaryKeySelective(T t);
	int updateExcludeVersionByPrimaryKeySelective(T t);
	int delete(T t);
	int deleteByKey(PK id);
}
