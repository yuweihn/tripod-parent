package com.assist4j.dao.mybatis.provider;


import org.apache.ibatis.jdbc.SQL;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import java.lang.reflect.Field;
import java.util.Map;


/**
 * @author yuwei
 */
public class DeleteSqlProvider {

	public <T>String delete(T t) throws IllegalAccessException {
		if (t == null) {
			return null;
		}
		
		Class<?> entityClass = t.getClass();
		final Table table = entityClass.getAnnotation(Table.class);
		if (table == null || table.name() == null || "".equals(table.name().trim())) {
			throw new RuntimeException("Table name is not found.");
		}
		
		final Field[] allFields = entityClass.getDeclaredFields();
		return new SQL() {{
			DELETE_FROM(table.name().trim());
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
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is missed.");
			}
		}}.toString();
	}
	
	@SuppressWarnings("unchecked")
	public <PK, T>String deleteByKey(Map<String, Object> param) throws IllegalAccessException {
		final PK id = (PK) param.get("param1");
		Class<T> entityClass = (Class<T>) param.get("param2");
		final Table table = entityClass.getAnnotation(Table.class);
		if (table == null || table.name() == null || "".equals(table.name().trim())) {
			throw new RuntimeException("Table name is not found.");
		}

		final Field[] allFields = entityClass.getDeclaredFields();
		return new SQL() {{
			DELETE_FROM(table.name().trim());
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
					WHERE("`" + colName + "` = " + id);
					whereSet = true;
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is missed.");
			}
		}}.toString();
	}
}
