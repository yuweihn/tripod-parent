package com.assist4j.dao.mybatis.util;


import java.lang.reflect.Field;
import javax.persistence.Column;


/**
 * @author yuwei
 */
public abstract class MapperUtil {
	public static String toSelectSql(Class<?> clz) {
		StringBuilder builder = new StringBuilder("");
		Field[] allFields = clz.getDeclaredFields();
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
			builder.append(",").append(colName).append(" as ").append(field.getName());
		}
		if (builder.length() > 0) {
			builder.deleteCharAt(0);
		}
		return builder.toString();
	}
}

