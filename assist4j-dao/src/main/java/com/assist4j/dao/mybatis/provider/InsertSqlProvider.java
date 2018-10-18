package com.assist4j.dao.mybatis.provider;


import org.apache.ibatis.jdbc.SQL;

import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public class InsertSqlProvider {
	public <T>String insert(T t) {
		Class<?> entityClass = t.getClass();
		final Table table = entityClass.getAnnotation(Table.class);
		if (table == null || table.name() == null || "".equals(table.name().trim())) {
			throw new RuntimeException("Table name is not found.");
		}

		final Field[] allFields = entityClass.getDeclaredFields();
		return new SQL() {{
			INSERT_INTO(table.name().trim());
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
				
				/**
				 * 如果使用数据库自增主键，此处生成的SQL中排除该字段
				 */
				Id idAnn = field.getAnnotation(Id.class);
				GeneratedValue genAnn = field.getAnnotation(GeneratedValue.class);
				if (idAnn != null && (genAnn == null || genAnn.strategy() == GenerationType.AUTO)) {
					continue;
				}
				
				VALUES("`" + colName + "`", "#{" + field.getName() + "}");
			}
		}}.toString();
	}
	
	public <T>String insertSelective(final T t) throws IllegalAccessException {
		Class<?> entityClass = t.getClass();
		final Table table = entityClass.getAnnotation(Table.class);
		if (table == null || table.name() == null || "".equals(table.name().trim())) {
			throw new RuntimeException("Table name is not found.");
		}

		final Field[] allFields = entityClass.getDeclaredFields();
		return new SQL() {{
			INSERT_INTO(table.name().trim());
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
				
				/**
				 * 如果使用数据库自增主键，此处生成的SQL中排除该字段
				 */
				Id idAnn = field.getAnnotation(Id.class);
				GeneratedValue genAnn = field.getAnnotation(GeneratedValue.class);
				if (idAnn != null && (genAnn == null || genAnn.strategy() == GenerationType.AUTO)) {
					continue;
				}
				
				VALUES("`" + colName + "`", "#{" + field.getName() + "}");
			}
		}}.toString();
	}
	
	public <T>String batchInsert(Map<String, List<T>> param) {
		List<T> list = param.get("param1");
		T t = list.get(0);
		Class<?> entityClass = t.getClass();
		Table table = entityClass.getAnnotation(Table.class);
		if (table == null || table.name() == null || "".equals(table.name().trim())) {
			throw new RuntimeException("Table name is not found.");
		}
		
		List<Field> persistFieldList = new ArrayList<Field>();
		Field[] allFields = entityClass.getDeclaredFields();
		for (Field field: allFields) {
			Column column = field.getAnnotation(Column.class);
			if (column == null) {
				continue;
			}
			
			String colName = column.name();
			if (colName == null || "".equals(colName.trim())) {
				continue;
			}
			
			/**
			 * 如果使用数据库自增主键，此处生成的SQL中排除该字段
			 */
			Id idAnn = field.getAnnotation(Id.class);
			GeneratedValue genAnn = field.getAnnotation(GeneratedValue.class);
			if (idAnn != null && (genAnn == null || genAnn.strategy() == GenerationType.AUTO)) {
				continue;
			}
			
			persistFieldList.add(field);
		}
		
		StringBuilder builder = new StringBuilder("insert into ");
		builder.append(table.name().trim()).append(" (");
		for (int i = 0; i < persistFieldList.size(); i++) {
			Field field = persistFieldList.get(i);
			Column col = field.getAnnotation(Column.class);
			if (i == persistFieldList.size() - 1) {
				builder.append("`").append(col.name().trim()).append("`");
			} else {
				builder.append("`").append(col.name().trim()).append("`").append(", ");
			}
		}
		builder.append(" ) values ");
		for (int j = 0; j < list.size(); j++) {
			builder.append("(");
			for (int i = 0; i < persistFieldList.size(); i++) {
				Field field = persistFieldList.get(i);
				if (i == persistFieldList.size() - 1) {
					builder.append("#{").append("list[" + j + "]." + field.getName()).append("} ");
				} else {
					builder.append("#{").append("list[" + j + "]." + field.getName()).append("}, ");
				}
			}
			if (j == list.size() - 1) {
				builder.append(") ");
			} else {
				builder.append("), ");
			}
		}
		return builder.toString();
	}
}
