package com.assist4j.dao.mybatis.provider;


import org.apache.ibatis.jdbc.SQL;

import javax.persistence.Id;
import javax.persistence.Version;

import java.lang.reflect.Field;
import java.util.List;


/**
 * @author yuwei
 */
public class UpdateSqlProvider extends AbstractProvider {
	public <T>String updateByPrimaryKey(final T t) throws IllegalAccessException {
		Class<?> entityClass = t.getClass();
		final String tableName = getTableName(entityClass);

		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			UPDATE(tableName);
			boolean whereSet = false;
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
				
				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					WHERE("`" + fc.getColumnName() + "` = #{" + field.getName() + "}");
					whereSet = true;
				} else {
					SET("`" + fc.getColumnName() + "`" + " = #{" + field.getName() + "} ");
				}
				
				Version version = field.getAnnotation(Version.class);
				if (version != null) {
					WHERE("`" + fc.getColumnName() + "` = #{" + field.getName() + "}");
					
					int val = field.getInt(t);
					SET("`" + fc.getColumnName() + "`" + " = " + (val + 1) + " ");
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is missed.");
			}
		}}.toString();
	}

	public <T>String updateByPrimaryKeySelective(final T t) throws IllegalAccessException {
		Class<?> entityClass = t.getClass();
		final String tableName = getTableName(entityClass);

		final List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			UPDATE(tableName);
			boolean whereSet = false;
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();

				field.setAccessible(true);
				Object o = field.get(t);
				if (o == null) {
					continue;
				}
				
				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					WHERE("`" + fc.getColumnName() + "` = #{" + field.getName() + "}");
					whereSet = true;
				} else {
					SET("`" + fc.getColumnName() + "`" + "= #{" + field.getName() + "} ");
				}
				
				Version version = field.getAnnotation(Version.class);
				if (version != null) {
					WHERE("`" + fc.getColumnName() + "` = #{" + field.getName() + "}");
					
					int val = field.getInt(t);
					SET("`" + fc.getColumnName() + "`" + " = " + (val + 1) + " ");
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is missed.");
			}
		}}.toString();
	}
}

