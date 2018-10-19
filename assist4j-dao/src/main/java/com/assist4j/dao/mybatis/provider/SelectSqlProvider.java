package com.assist4j.dao.mybatis.provider;


import org.apache.ibatis.jdbc.SQL;

import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public class SelectSqlProvider extends AbstractProvider {

	@SuppressWarnings("unchecked")
	public <PK, T>String selectOneById(Map<String, Object> param) throws IllegalAccessException {
		final PK id = (PK) param.get("param1");
		Class<T> entityClass = (Class<T>) param.get("param2");
		final String tableName = getTableName(entityClass);

		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			boolean whereSet = false;
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
				SELECT(fc.getColumnName() + " as " + field.getName());
				
				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					WHERE(fc.getColumnName() + " = " + id);
					whereSet = true;
				}
			}
			FROM(tableName);
			if (!whereSet) {
				throw new IllegalAccessException("'where' is missed.");
			}
		}}.toString();
	}
	
	@SuppressWarnings("unchecked")
	public <T>String selectListOrderBy(Map<String, Object> param) throws IllegalAccessException {
		final Map<String, Object> whereMap = (Map<String, Object>) param.get("param1");
		final String orderBy = (String) param.get("param2");
		Class<T> entityClass = (Class<T>) param.get("param3");
		final String tableName = getTableName(entityClass);

		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			boolean whereSet = false;
			FROM(tableName);
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
				SELECT(fc.getColumnName() + " as " + field.getName());
				
				field.setAccessible(true);
				Object obj = field.get(whereMap);
				if (obj != null) {
					//增加空字符串的判断
					if (obj instanceof String) {
						String str = (String) obj;
						if (str != null) {
							WHERE(fc.getColumnName() + " = " + str);
						}
					} else {
						WHERE(fc.getColumnName() + " = " + obj);
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

