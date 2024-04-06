package com.yuweix.tripod.dao.mybatis.provider;


import com.yuweix.tripod.dao.PersistUtil;
import org.apache.ibatis.jdbc.SQL;

import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.lang.reflect.Field;
import java.util.List;


/**
 * @author yuwei
 */
public class InsertSqlProvider extends AbstractProvider {
	public <T>String insert(T t) throws IllegalAccessException {
		return toInsertSql(t, false);
	}

	public <T>String insertSelective(T t) throws IllegalAccessException {
		return toInsertSql(t, true);
	}

	private <T>String toInsertSql(T t, boolean selective) throws IllegalAccessException {
		Class<?> entityClass = t.getClass();
		String tbName = PersistUtil.getTableName(entityClass);

		List<PersistUtil.FieldCol> fcList = PersistUtil.getPersistFieldList(entityClass);
		return new SQL() {{
			for (PersistUtil.FieldCol fc: fcList) {
				Field field = fc.getField();
				field.setAccessible(true);

				if (selective) {
					Object o = field.get(t);
					if (o == null) {
						continue;
					}
				}

				/**
				 * 如果使用数据库自增主键，此处生成的SQL中排除该字段
				 */
				Id idAnn = field.getAnnotation(Id.class);
				GeneratedValue genAnn = field.getAnnotation(GeneratedValue.class);
				if (idAnn != null && genAnn != null && genAnn.strategy() == GenerationType.IDENTITY) {
					continue;
				}

				VALUES("`" + fc.getColumnName() + "`", "#{" + field.getName() + "}");
			}
			INSERT_INTO(tbName);
		}}.toString();
	}
}
