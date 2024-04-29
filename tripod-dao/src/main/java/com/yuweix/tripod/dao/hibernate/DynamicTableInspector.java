package com.yuweix.tripod.dao.hibernate;


import com.yuweix.tripod.sharding.context.TableHolder;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 动态修改表名拦截器
 * @author yuwei
 */
public class DynamicTableInspector implements StatementInspector {
	private static final Logger log = LoggerFactory.getLogger(DynamicTableInspector.class);

	public DynamicTableInspector() {

	}

	@Override
	public String inspect(String sql) {
		final String srcName = TableHolder.getSrcName();
		final String targetName = TableHolder.getTargetName();
		if (srcName == null || targetName == null) {
			return sql;
		}
		log.info("Original SQL: {}", sql);
		String actualSql = sql.replaceAll(srcName, targetName);
		log.info("Actual SQL: {}", actualSql);
		return actualSql;
	}
}
