package com.assist4j.dao.mybatis.provider;


import com.assist4j.dao.mybatis.util.MapperUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.persistence.Table;


/**
 * @author wei
 */
public abstract class AbstractProvider {
	private static Map<String, String> TABLE_NAME_MAP = new ConcurrentHashMap<String, String>();
	private static Map<String, String> SELECT_SQL_MAP = new ConcurrentHashMap<String, String>();

	private static Lock TN_LOCK = new ReentrantLock();
	private static Lock SS_LOCK = new ReentrantLock();

	protected String getTableName(Class<?> clz) {
		String className = clz.getName();
		String tableName = TABLE_NAME_MAP.get(className);
		if (tableName == null) {
			try {
				TN_LOCK.tryLock();
				if (tableName == null) {
					Table table = clz.getAnnotation(Table.class);
					if (table == null || table.name() == null || "".equals(table.name().trim())) {
						throw new RuntimeException("Table name is not found.");
					}
					tableName = table.name().trim();
					TABLE_NAME_MAP.put(className, tableName);
				}
			} finally {
				TN_LOCK.unlock();
			}
		}
		return tableName;
	}

	protected String getSelectSql(Class<?> clz) {
		String className = clz.getName();
		String selectSql = SELECT_SQL_MAP.get(className);
		if (selectSql == null) {
			try {
				SS_LOCK.tryLock();
				if (selectSql == null) {
					selectSql = MapperUtil.toSelectSql(clz);
					SELECT_SQL_MAP.put(className, selectSql);
				}
			} finally {
				SS_LOCK.unlock();
			}
		}
		return selectSql;
	}
}
