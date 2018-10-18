package com.assist4j.dao.mybatis.provider;


import com.assist4j.dao.mybatis.util.MapperUtil;
import org.apache.ibatis.jdbc.SQL;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public class SelectSqlProvider {

	@SuppressWarnings("unchecked")
	public <PK, T>String selectOneById(Map<String, Object> param) throws IllegalAccessException {
		PK id = (PK) param.get("param1");
		Class<T> entityClass = (Class<T>) param.get("param2");
		Table table = entityClass.getAnnotation(Table.class);
		if (table == null || table.name() == null || "".equals(table.name().trim())) {
			throw new RuntimeException("Table name is not found.");
		}
		
		List<Field> allFields = MapperUtil.getAllFieldsList(entityClass);
		return new SQL() {{
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
				SELECT(colName + " as " + field.getName());
				
				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					WHERE(colName + " = " + id);
					whereSet = true;
				}
			}
			FROM(table.name().trim());
			if (!whereSet) {
				throw new IllegalAccessException("'where' is missed.");
			}
		}}.toString();
	}
	
	@SuppressWarnings("unchecked")
	public <T>String selectListOrderBy(Map<String, Object> param) throws IllegalAccessException {
		Map<String, Object> whereMap = (Map<String, Object>) param.get("param1");
		String orderBy = (String) param.get("param2");
		Class<T> entityClass = (Class<T>) param.get("param3");
		
		Table table = entityClass.getAnnotation(Table.class);
		if (table == null || table.name() == null || "".equals(table.name().trim())) {
			throw new RuntimeException("Table name is not found.");
		}
		
		List<Field> allFields = MapperUtil.getAllFieldsList(entityClass);
		return new SQL() {{
			boolean whereSet = false;
			FROM(table.name().trim());
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
				
				SELECT(colName + " as " + field.getName());
				
				field.setAccessible(true);
				Object obj = field.get(whereMap);
				if (obj != null) {
					//增加空字符串的判断
					if (obj instanceof String) {
						String str = (String) obj;
						if (str != null) {
							WHERE(colName + " = " + str);
						}
					} else {
						WHERE(colName + " = " + obj);
					}
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is missed.");
			}
			if (orderBy != null && !"".equals(orderBy)) {
				ORDER_BY(orderBy);
			}
		}}.toString();
	}
}

