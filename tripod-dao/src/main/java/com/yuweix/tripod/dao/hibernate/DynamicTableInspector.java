package com.yuweix.tripod.dao.hibernate;


import com.yuweix.tripod.dao.sharding.DynamicTableTL;
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
		log.info("Original SQL: {}", sql);
		final String srcName = DynamicTableTL.getSrcName();
		final String targetName = DynamicTableTL.getTargetName();
		String actualSql = sql;
		if (srcName != null && targetName != null) {
			actualSql = sql.replaceAll(srcName, targetName);
		}
		log.info("Actual SQL: {}", actualSql);
		return actualSql;
	}
}
