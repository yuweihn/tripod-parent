package com.assist4j.dao.mybatis;


import java.io.Serializable;

import com.assist4j.dao.mybatis.provider.DeleteSqlProvider;
import org.apache.ibatis.annotations.DeleteProvider;


/**
 * @author yuwei
 */
public interface DeleteMapper<T extends Serializable, PK extends Serializable> {
	@DeleteProvider(type = DeleteSqlProvider.class, method = "delete")
	int delete(T t);

	@DeleteProvider(type = DeleteSqlProvider.class, method = "deleteByKey")
	int deleteByKey(PK id, Class<T> clz);
}
