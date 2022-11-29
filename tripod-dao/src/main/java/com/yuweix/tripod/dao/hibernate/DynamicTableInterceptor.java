package com.yuweix.tripod.dao.hibernate;


import org.hibernate.EmptyInterceptor;


/**
 * 动态修改表名拦截器
 * @author yuwei
 */
public class DynamicTableInterceptor extends EmptyInterceptor {
	public DynamicTableInterceptor() {

	}

	@Override
	public String onPrepareStatement(String sql) {
		final String srcName = DynamicTableTL.getSrcName();
		final String destName = DynamicTableTL.getDestName();
		if (srcName == null || destName == null) {
			return sql;
		}
		return sql.replaceAll(srcName, destName);
	}
}
