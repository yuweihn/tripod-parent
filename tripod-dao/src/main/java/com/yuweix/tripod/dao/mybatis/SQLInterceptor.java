package com.yuweix.tripod.dao.mybatis;


import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;

import java.lang.reflect.Field;
import java.sql.Connection;


/**
 * @author yuwei
 */
@Intercepts({
		@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class SQLInterceptor implements Interceptor {
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		String sql = statementHandler.getBoundSql().getSql();
		String modifiedSql = modifySql(sql);
		Field sqlField = BoundSql.class.getDeclaredField("sql");
		sqlField.setAccessible(true);
		sqlField.set(statementHandler.getBoundSql(), modifiedSql);
		return invocation.proceed();
	}

	private String modifySql(String originalSql) {
		return originalSql;
	}
}
