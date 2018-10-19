package com.assist4j.dao.mybatis;


import com.assist4j.dao.mybatis.provider.SelectSqlProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

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
	 * @param clazz
	 * @return
	 */
	@SelectProvider(type = SelectSqlProvider.class, method = "selectCount")
	int selectCount(@Param("where") Map<String, Object> where, @Param("clazz") Class<T> clazz);

	/**
	 * @param where                   where中的key为表的字段，不是对象的属性
	 * @param orderBy
	 * @param clazz
	 * @return
	 */
	@SelectProvider(type = SelectSqlProvider.class, method = "selectList")
	List<T> selectList(@Param("where") Map<String, Object> where, @Param("orderBy") String orderBy, @Param("clazz") Class<T> clazz);

	@SelectProvider(type = SelectSqlProvider.class, method = "selectList")
	List<T> selectPageList(@Param("where") Map<String, Object> where, @Param("pageNo") int pageNo
            , @Param("pageSize") int pageSize, @Param("orderBy") String orderBy, @Param("clazz") Class<T> clazz);
}

