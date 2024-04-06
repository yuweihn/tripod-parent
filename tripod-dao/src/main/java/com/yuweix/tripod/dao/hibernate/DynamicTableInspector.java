package com.yuweix.tripod.dao.hibernate;


import com.yuweix.tripod.dao.sharding.DynamicTableTL;
import org.hibernate.resource.jdbc.spi.StatementInspector;


/**
 * 动态修改表名拦截器
 * @author yuwei
 */
public class DynamicTableInspector implements StatementInspector {
	public DynamicTableInspector() {

	}

	@Override
	public String inspect(String sql) {
		final String srcName = DynamicTableTL.getSrcName();
		final String targetName = DynamicTableTL.getTargetName();
		if (srcName == null || targetName == null) {
			return sql;
		}
		return sql.replaceAll(srcName, targetName);
	}
}
