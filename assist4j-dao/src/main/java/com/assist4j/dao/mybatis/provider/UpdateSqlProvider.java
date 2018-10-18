package com.assist4j.dao.mybatis.provider;


import org.apache.ibatis.jdbc.SQL;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import java.lang.reflect.Field;


/**
 * @author yuwei
 */
public class UpdateSqlProvider {
	public <T>String updateByPrimaryKey(final T t) throws IllegalAccessException {
		Class<?> entityClass = t.getClass();
		final Table table = entityClass.getAnnotation(Table.class);
		if (table == null || table.name() == null || "".equals(table.name().trim())) {
			throw new RuntimeException("Table name is not found.");
		}

		final Field[] allFields = entityClass.getDeclaredFields();
		return new SQL() {{
			UPDATE(table.name().trim());
			boolean whereSet = false;
			for (Field field: allFields) {
				Column column = field.getAnnotation(Column.class);
				if (column == null) {
					continue;
				}
				
				String colName = column.name();
				if (colName == null || "".equals(colName.trim())) {
					continue;
				}
				colName = colName.trim();
				
				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					WHERE("`" + colName + "` = #{" + field.getName() + "}");
					whereSet = true;
				} else {
					SET("`" + colName + "`" + " = #{" + field.getName() + "} ");
				}
				
				Version version = field.getAnnotation(Version.class);
				if (version != null) {
					WHERE("`" + colName + "` = #{" + field.getName() + "}");
					
					int val = field.getInt(t);
					SET("`" + colName + "`" + " = " + (val + 1) + " ");
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is missed.");
			}
		}}.toString();
	}

	public <T>String updateByPrimaryKeySelective(final T t) throws IllegalAccessException {
		Class<?> entityClass = t.getClass();
		final Table table = entityClass.getAnnotation(Table.class);
		if (table == null || table.name() == null || "".equals(table.name().trim())) {
			throw new RuntimeException("Table name is not found.");
		}

		final Field[] allFields = entityClass.getDeclaredFields();
		return new SQL() {{
			UPDATE(table.name().trim());
			boolean whereSet = false;
			for (Field field: allFields) {
				field.setAccessible(true);
				Object o = field.get(t);
				if (o == null) {
					continue;
				}
				
				Column column = field.getAnnotation(Column.class);
				if (column == null) {
					continue;
				}
				
				String colName = column.name();
				if (colName == null || "".equals(colName.trim())) {
					continue;
				}
				colName = colName.trim();
				
				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					WHERE("`" + colName + "` = #{" + field.getName() + "}");
					whereSet = true;
				} else {
					SET("`" + colName + "`" + "= #{" + field.getName() + "} ");
				}
				
				Version version = field.getAnnotation(Version.class);
				if (version != null) {
					WHERE("`" + colName + "` = #{" + field.getName() + "}");
					
					int val = field.getInt(t);
					SET("`" + colName + "`" + " = " + (val + 1) + " ");
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is missed.");
			}
		}}.toString();
	}
}

