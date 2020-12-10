package com.yuweix.assist4j.dao.mybatis.provider;


import com.yuweix.assist4j.dao.mybatis.where.Criteria;
import com.yuweix.assist4j.dao.sharding.Sharding;
import org.apache.ibatis.jdbc.SQL;

import javax.persistence.Id;
import javax.persistence.Version;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public class UpdateSqlProvider extends AbstractProvider {
	public <T>String updateByPrimaryKey(T t) throws IllegalAccessException {
		return toUpdateByPrimaryKeySql(t, false);
	}

	public <T>String updateByPrimaryKeySelective(T t) throws IllegalAccessException {
		return toUpdateByPrimaryKeySql(t, true);
	}

	private <T>String toUpdateByPrimaryKeySql(T t, boolean selective) throws IllegalAccessException {
		Class<?> entityClass = t.getClass();
		StringBuilder tableNameBuilder = new StringBuilder(getTableName(entityClass));

		List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			boolean whereSet = false;
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
				field.setAccessible(true);

				String shardingIndex = getShardingIndex(field, t);
				if (shardingIndex != null) {
					tableNameBuilder.append("_").append(shardingIndex);
					/**
					 * 分片字段，必须放在where子句中，且一定不能修改
					 */
					WHERE("`" + fc.getColumnName() + "` = #{" + field.getName() + "}");
					continue;
				}

				if (selective) {
					Object o = field.get(t);
					if (o == null) {
						continue;
					}
				}

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
			UPDATE(tableNameBuilder.toString());
		}}.toString();
	}

	public <T>String updateByCriteria(Map<String, Object> param) throws IllegalAccessException {
		return toUpdateByCriteriaSql(param, false);
	}

	public <T>String updateByCriteriaSelective(Map<String, Object> param) throws IllegalAccessException {
		return toUpdateByCriteriaSql(param, true);
	}

	@SuppressWarnings("unchecked")
	private <T>String toUpdateByCriteriaSql(Map<String, Object> param, boolean selective) throws IllegalAccessException {
		T t = (T) param.get("t");
		Class<?> entityClass = t.getClass();
		List<String> excludeFields = (List<String>) param.get("excludeFields");
		Criteria criteria = (Criteria) param.get("criteria");
		if (criteria == null || criteria.getParams() == null || criteria.getParams().size() <= 0) {
			throw new IllegalAccessException("'where' is missed.");
		}
		StringBuilder tableNameBuilder = new StringBuilder(getTableName(entityClass));
		List<FieldColumn> fcList = getPersistFieldList(entityClass);
		return new SQL() {{
			for (FieldColumn fc: fcList) {
				Field field = fc.getField();
				field.setAccessible(true);

				String shardingIndex = getShardingIndex(field, t);
				if (shardingIndex != null) {
					tableNameBuilder.append("_").append(shardingIndex);
					/**
					 * 分片字段，必须放在where子句中，且一定不能修改
					 */
					WHERE("`" + fc.getColumnName() + "` = #{" + field.getName() + "}");
					continue;
				}

				if (excludeFields != null && excludeFields.contains(field.getName())) {
					continue;
				}

				if (selective) {
					Object o = field.get(t);
					if (o == null) {
						continue;
					}
				}

				SET("`" + fc.getColumnName() + "`" + " = #{t." + field.getName() + "} ");

				Version version = field.getAnnotation(Version.class);
				if (version != null) {
					WHERE("`" + fc.getColumnName() + "` = #{t." + field.getName() + "}");

					int val = field.getInt(t);
					SET("`" + fc.getColumnName() + "`" + " = " + (val + 1) + " ");
				}
			}
			WHERE(criteria.toSql());
			UPDATE(tableNameBuilder.toString());
		}}.toString();
	}
}

