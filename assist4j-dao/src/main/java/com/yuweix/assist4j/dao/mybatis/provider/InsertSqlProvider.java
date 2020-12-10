package com.yuweix.assist4j.dao.mybatis.provider;


import com.yuweix.assist4j.dao.sharding.Sharding;
import com.yuweix.assist4j.dao.sharding.Strategy;
import org.apache.ibatis.jdbc.SQL;

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
public class InsertSqlProvider extends AbstractProvider {
	public <T>String insert(T t) {
		Class<?> entityClass = t.getClass();
		StringBuilder tableNameBuilder = new StringBuilder(getTableName(entityClass));

		List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();

				String shardingIndex = getShardingIndex(field, t);
				if (shardingIndex != null) {
					tableNameBuilder.append("_").append(shardingIndex);
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
			INSERT_INTO(tableNameBuilder.toString());
		}}.toString();
	}
	
	public <T>String insertSelective(T t) throws IllegalAccessException {
		Class<?> entityClass = t.getClass();
		StringBuilder tableNameBuilder = new StringBuilder(getTableName(entityClass));

		List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
				field.setAccessible(true);

				String shardingIndex = getShardingIndex(field, t);
				if (shardingIndex != null) {
					tableNameBuilder.append("_").append(shardingIndex);
				}

				Object o = field.get(t);
				if (o == null) {
					continue;
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
			INSERT_INTO(tableNameBuilder.toString());
		}}.toString();
	}
	
	public <T>String batchInsert(Map<String, List<T>> param) {
		List<T> list = param.get("param1");
		T t = list.get(0);
		Class<?> entityClass = t.getClass();
		StringBuilder tableNameBuilder = new StringBuilder(getTableName(entityClass));

		List<FieldColumn> fcList = getPersistFieldList(entityClass);
		List<Field> persistFieldList = new ArrayList<Field>();
		for (FieldColumn fc: fcList) {
			Field field = fc.getField();

			String shardingIndex = getShardingIndex(field, t);
			if (shardingIndex != null) {
				tableNameBuilder.append("_").append(shardingIndex);
			}
			
			/**
			 * 如果使用数据库自增主键，此处生成的SQL中排除该字段
			 */
			Id idAnn = field.getAnnotation(Id.class);
			GeneratedValue genAnn = field.getAnnotation(GeneratedValue.class);
			if (idAnn != null && genAnn != null && genAnn.strategy() == GenerationType.IDENTITY) {
				continue;
			}
			
			persistFieldList.add(field);
		}
		
		StringBuilder builder = new StringBuilder("insert into ");
		builder.append(tableNameBuilder.toString()).append(" (");
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

	private String getShardingIndex(Field field, Object t) {
		Sharding sharding = field.getAnnotation(Sharding.class);
		if (sharding == null) {
			return null;
		}
		try {
			Strategy shardingStrategy = (Strategy) sharding.strategy().newInstance();
			if (!field.isAccessible()) {
				field.setAccessible(true);
			}
			String shardingIndex = shardingStrategy.getShardingIndex(field.get(t)
					, sharding.suffixLength(), sharding.shardingSize());
			return shardingIndex;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
