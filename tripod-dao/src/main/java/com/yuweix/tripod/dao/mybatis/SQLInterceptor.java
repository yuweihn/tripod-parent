package com.yuweix.tripod.dao.mybatis;


import com.yuweix.tripod.sharding.context.TableHolder;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;


/**
 * @author yuwei
 */
@Intercepts({
		@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class SQLInterceptor implements Interceptor {
	private static final Logger log = LoggerFactory.getLogger(SQLInterceptor.class);

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		BoundSql boundSql = statementHandler.getBoundSql();
		String modifiedSql = modifySql(boundSql.getSql());
		Field sqlField = BoundSql.class.getDeclaredField("sql");
		sqlField.setAccessible(true);
		sqlField.set(boundSql, modifiedSql);
		return invocation.proceed();
	}

	private String modifySql(String originalSql) {
		log.info("Original SQL: {}", originalSql);
		final String srcName = TableHolder.getSrcName();
		final String targetName = TableHolder.getTargetName();
		String actualSql = originalSql;
		if (srcName != null && targetName != null) {
			actualSql = originalSql.replaceAll(srcName, targetName);
		}
		log.info("Actual SQL: {}", actualSql);
		return actualSql;
	}
}
