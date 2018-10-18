package com.assist4j.dao.mybatis;


import java.io.Serializable;

import com.assist4j.dao.mybatis.provider.UpdateSqlProvider;
import org.apache.ibatis.annotations.UpdateProvider;


/**
 * @author yuwei
 */
public interface UpdateMapper<T extends Serializable, PK extends Serializable> {
	@UpdateProvider(type = UpdateSqlProvider.class, method = "updateByPrimaryKey")
	int updateByPrimaryKey(T t);

	@UpdateProvider(type = UpdateSqlProvider.class, method = "updateByPrimaryKeySelective")
	int updateByPrimaryKeySelective(T t);
}
