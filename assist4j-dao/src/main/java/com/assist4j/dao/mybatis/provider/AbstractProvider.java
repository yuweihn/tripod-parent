package com.assist4j.dao.mybatis.provider;


import com.assist4j.dao.mybatis.util.MapperUtil;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.Table;


/**
 * @author wei
 */
public class AbstractProvider {
	private String tableName;
	private String selectSql;

	private Lock tnLock = new ReentrantLock();
	private Lock sqlLock = new ReentrantLock();

	protected String getTableName(Class<?> clz) {
		if (tableName == null) {
			try {
				tnLock.tryLock();
				if (tableName == null) {
					Table table = clz.getAnnotation(Table.class);
					if (table == null || table.name() == null || "".equals(table.name().trim())) {
						throw new RuntimeException("Table name is not found.");
					}
					tableName = table.name().trim();
				}
			} finally {
				tnLock.unlock();
			}
		}
		return tableName;
	}

	protected String getSelectSql(Class<?> clz) {
		if (selectSql == null) {
			try {
				sqlLock.tryLock();
				if (selectSql == null) {
					selectSql = MapperUtil.toSelectSql(clz);
				}
			} finally {
				sqlLock.unlock();
			}
		}
		return selectSql;
	}
}
