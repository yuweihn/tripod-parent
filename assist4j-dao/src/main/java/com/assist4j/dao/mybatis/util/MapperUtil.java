package com.assist4j.dao.mybatis.util;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;


/**
 * @author yuwei
 */
public abstract class MapperUtil {
	/**
	 * Gets all fields of the given class and its parents (if any).
	 * @return
	 */
	public static List<Field> getAllFieldsList(Class<?> clz) {
		final List<Field> allFields = new ArrayList<Field>();
		Class<?> currentClass = clz;
		while (currentClass != null) {
			final Field[] declaredFields = currentClass.getDeclaredFields();
			for (Field field : declaredFields) {
				allFields.add(field);
			}
			currentClass = currentClass.getSuperclass();
		}
		return allFields;
	}

	public static String toSelectSql(Class<?> clz) {
		StringBuilder builder = new StringBuilder("");
		List<Field> allFields = getAllFieldsList(clz);
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

