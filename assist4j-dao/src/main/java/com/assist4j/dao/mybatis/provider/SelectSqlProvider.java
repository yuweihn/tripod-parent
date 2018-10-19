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
	public <T>String selectCount(Map<String, Object> param) {
		final Map<String, Object> whereMap = (Map<String, Object>) param.get("where");
		Class<T> entityClass = (Class<T>) param.get("clazz");
		final String tableName = getTableName(entityClass);

		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			FROM(tableName);
			SELECT("count(1) as cnt");
			if (whereMap != null) {
				for (FieldColumn fc : fcList) {
					Object obj = whereMap.get(fc.getColumnName());
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
			}
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <T>String selectList(Map<String, Object> param) {
		final Map<String, Object> whereMap = (Map<String, Object>) param.get("where");
		final String orderBy = (String) param.get("orderBy");
		Class<T> entityClass = (Class<T>) param.get("clazz");
		Integer pageNo0 = (Integer) param.get("pageNo");
		Integer pageSize0 = (Integer) param.get("pageSize");
		final String tableName = getTableName(entityClass);

		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		StringBuilder sql = new StringBuilder(new SQL() {{
			FROM(tableName);
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
				SELECT(fc.getColumnName() + " as " + field.getName());

				if (whereMap != null) {
					Object obj = whereMap.get(fc.getColumnName());
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
			}
			if (orderBy != null && !"".equals(orderBy)) {
				ORDER_BY(orderBy);
			}
		}}.toString());

		if (pageNo0 != null && pageSize0 != null) {
			int pageNo = pageNo0 <= 0 ? 1 : pageNo0;
			int pageSize = pageSize0 <= 0 ? 10 : pageSize0;

			sql.append(" limit ").append((pageNo - 1) * pageSize).append(", ").append(pageSize);
		}
		return sql.toString();
	}
}

