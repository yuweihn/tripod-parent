package com.yuweix.tripod.dao.hibernate;


import org.hibernate.EmptyInterceptor;


/**
 * 动态修改表名
 * @author yuwei
 */
public class DynamicTableInterceptor extends EmptyInterceptor {
	private String srcName;
	private String destName;


	public DynamicTableInterceptor() {

	}
	public DynamicTableInterceptor(String srcName, String destName) {
		this.srcName = srcName;
		this.destName = destName;
	}

	@Override
	public String onPrepareStatement(String sql) {
		if (srcName == null || destName == null) {
			return sql;
		}
		return sql.replaceAll(srcName, destName);
	}
}
