package com.assist4j.dao.mybatis.provider;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;
import javax.persistence.Table;


/**
 * @author yuwei
 */
public abstract class AbstractProvider {
	private static Map<String, String> TABLE_NAME_MAP = new ConcurrentHashMap<String, String>();
	private static Map<String, String> SELECT_SQL_MAP = new ConcurrentHashMap<String, String>();
	private static Map<String, List<FieldColumn>> PERSIST_FIELD_MAP = new ConcurrentHashMap<String, List<FieldColumn>>();


	/**
	 * 根据持久化类获取对应的关系表表名。
	 * @param clz
	 * @return
	 */
	protected String getTableName(Class<?> clz) {
		String className = clz.getName();
		String tableName = TABLE_NAME_MAP.get(className);
		if (tableName == null) {
			Table table = clz.getAnnotation(Table.class);
			if (table == null || table.name() == null || "".equals(table.name().trim())) {
				throw new RuntimeException("Table name is not found.");
			}
			tableName = table.name().trim();
			TABLE_NAME_MAP.put(className, tableName);
		}
		return tableName;
	}

	/**
	 * 根据持久化类获取对应的所有需要持久化的属性SQL。
	 * eg：id as id, user_name as userName, create_time as createTime
	 * @param clz
	 * @return
	 */
	protected String getSelectSql(Class<?> clz) {
		String className = clz.getName();
		String selectSql = SELECT_SQL_MAP.get(className);
		if (selectSql == null) {
			StringBuilder builder = new StringBuilder("");
			List<FieldColumn> fcList = getPersistFieldList(clz);
			for (FieldColumn fc: fcList) {
				builder.append(",").append(fc.getColumnName()).append(" as ").append(fc.getField().getName());
			}
			if (builder.length() > 0) {
				builder.deleteCharAt(0);
			}
			selectSql = builder.toString();
			SELECT_SQL_MAP.put(className, selectSql);
		}
		return selectSql;
	}

	/**
	 * 根据持久化类获取对应的所有需要持久化的属性集合
	 * @param clz
	 * @return
	 */
	protected List<FieldColumn> getPersistFieldList(Class<?> clz) {
		String className = clz.getName();
		List<FieldColumn> fcList = PERSIST_FIELD_MAP.get(className);
		if (fcList == null) {
			fcList = getPersistFieldList0(clz);
			PERSIST_FIELD_MAP.put(className, fcList);
		}
		return fcList;
	}

	/**
	 * Gets all fields of the given class and its parents (if any).
	 * @return
	 */
	private static List<FieldColumn> getPersistFieldList0(Class<?> clz) {
		final List<Field> allFields = new ArrayList<Field>();
		Class<?> currentClass = clz;
		while (currentClass != null) {
			final Field[] declaredFields = currentClass.getDeclaredFields();
			for (Field field : declaredFields) {
				allFields.add(field);
			}
			currentClass = currentClass.getSuperclass();
		}
		
		List<FieldColumn> list = new ArrayList<FieldColumn>();
		for (Field field: allFields) {
			Column column = field.getAnnotation(Column.class);
			if (column == null) {
				continue;
			}

			String colName = column.name();
			if (colName == null || "".equals(colName.trim())) {
				continue;
			}
			list.add(new FieldColumn(colName.trim(), field));
		}
		return list;
	}

	public static class FieldColumn {
		private String columnName;
		private Field field;

		public FieldColumn(String columnName, Field field) {
			this.columnName = columnName;
			this.field = field;
		}

		public String getColumnName() {
			return columnName;
		}
		public Field getField() {
			return field;
		}
	}
}
