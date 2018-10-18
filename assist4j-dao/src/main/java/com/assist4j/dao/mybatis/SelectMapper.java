package com.assist4j.dao.mybatis;


import org.apache.ibatis.annotations.SelectProvider;
import com.wei.mycat.demo.mybatis.provider.SelectSqlProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public interface SelectMapper<T extends Serializable, PK extends Serializable> {
	@SelectProvider(type = SelectSqlProvider.class, method = "selectOneById")
	T selectOneById(PK id, Class<T> clazz);
	
	/**
	 * @param where                   where中的key为表的字段，不是对象的属性
	 * @param orderBy
	 * @param clazz
	 * @return
	 */
	@SelectProvider(type = SelectSqlProvider.class, method = "selectListOrderBy")
	List<T> selectListOrderBy(Map<String, Object> where, String orderBy, Class<T> clazz);
}

