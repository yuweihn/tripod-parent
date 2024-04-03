package com.yuweix.tripod.dao.mybatis.provider;


import com.yuweix.tripod.dao.PersistUtil;
import com.yuweix.tripod.dao.mybatis.where.Criteria;
import com.yuweix.tripod.dao.sharding.Sharding;
import org.apache.ibatis.jdbc.SQL;

import jakarta.persistence.Id;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public class DeleteSqlProvider extends AbstractProvider {

	public <T>String delete(T t) throws IllegalAccessException {
		Class<?> entityClass = t.getClass();
		String tbName = PersistUtil.getTableName(entityClass);
		StringBuilder tableNameBuilder = new StringBuilder(tbName);

		List<PersistUtil.FieldCol> fcList = PersistUtil.getPersistFieldList(entityClass);
		return new SQL() {{
			boolean whereSet = false;

			for (PersistUtil.FieldCol fc: fcList) {
				Field field = fc.getField();

				String shardingIndex = PersistUtil.getShardingIndex(field.getAnnotation(Sharding.class), tbName, PersistUtil.getFieldValue(field, t));
				Id idAnn = field.getAnnotation(Id.class);
				if (shardingIndex != null) {
					tableNameBuilder.append("_").append(shardingIndex);
					/**
					 * 分片字段，必须放在where子句中
					 */
					WHERE("`" + fc.getColumnName() + "` = #{" + field.getName() + "}");
					if (idAnn != null) {
						whereSet = true;
					}
					continue;
				}

				/**
				 * ID字段，必须放在where子句中
				 */
				if (idAnn != null) {
					WHERE("`" + fc.getColumnName() + "` = #{" + field.getName() + "}");
					whereSet = true;
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is required.");
			}
			DELETE_FROM(tableNameBuilder.toString());
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <PK, T>String deleteByKey(Map<String, Object> param) throws IllegalAccessException {
//		PK id = (PK) param.get("id");
		Class<T> entityClass = (Class<T>) param.get("clz");
		StringBuilder tableNameBuilder = new StringBuilder(PersistUtil.getTableName(entityClass));

		List<PersistUtil.FieldCol> fcList = PersistUtil.getPersistFieldList(entityClass);
		return new SQL() {{
			boolean hasSharding = false;
			boolean whereSet = false;

			for (PersistUtil.FieldCol fc: fcList) {
				Field field = fc.getField();

				Sharding sharding = field.getAnnotation(Sharding.class);
				if (sharding != null) {
					hasSharding = true;
					break;
				}

				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					WHERE("`" + fc.getColumnName() + "` = #{id}");
					whereSet = true;
				}
			}
			if (hasSharding) {
				throw new IllegalAccessException("'Sharding Value' is required.");
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is required.");
			}
			DELETE_FROM(tableNameBuilder.toString());
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <PK, T>String deleteByKeySharding(Map<String, Object> param) throws IllegalAccessException {
//		PK id = (PK) param.get("id");
		Class<T> entityClass = (Class<T>) param.get("clz");
		Object shardingVal = param.get("shardingVal");
		String tbName = PersistUtil.getTableName(entityClass);
		StringBuilder tableNameBuilder = new StringBuilder(tbName);

		List<PersistUtil.FieldCol> fcList = PersistUtil.getPersistFieldList(entityClass);
		return new SQL() {{
			boolean whereSet = false;

			for (PersistUtil.FieldCol fc: fcList) {
				Field field = fc.getField();

				String shardingIndex = PersistUtil.getShardingIndex(field.getAnnotation(Sharding.class), tbName, shardingVal);
				Id idAnn = field.getAnnotation(Id.class);
				if (shardingIndex != null) {
					tableNameBuilder.append("_").append(shardingIndex);
					/**
					 * 分片字段，必须放在where子句中
					 */
					WHERE("`" + fc.getColumnName() + "` = #{shardingVal}");
					if (idAnn != null) {
						whereSet = true;
					}
					continue;
				}

				if (idAnn != null) {
					WHERE("`" + fc.getColumnName() + "` = #{id}");
					whereSet = true;
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is required.");
			}
			DELETE_FROM(tableNameBuilder.toString());
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <T>String deleteByCriteria(Map<String, Object> param) throws IllegalAccessException {
		Class<T> entityClass = (Class<T>) param.get("clz");
		String tbName = PersistUtil.getTableName(entityClass);
		StringBuilder tableNameBuilder = new StringBuilder(tbName);
		Criteria criteria = (Criteria) param.get("criteria");
		if (criteria == null || criteria.getParams() == null || criteria.getParams().size() <= 0) {
			throw new IllegalAccessException("'where' is required.");
		}

		Object shardingVal = criteria.getShardingVal();
		List<PersistUtil.FieldCol> fcList = PersistUtil.getPersistFieldList(entityClass);
		return new SQL() {{
			for (PersistUtil.FieldCol fc: fcList) {
				Field field = fc.getField();
				Sharding sharding = field.getAnnotation(Sharding.class);
				if (sharding != null) {
					if (shardingVal == null) {
						throw new IllegalAccessException("'Sharding Value' is required.");
					}
					String shardingIndex = PersistUtil.getShardingIndex(sharding, tbName, shardingVal);
					if (shardingIndex != null) {
						tableNameBuilder.append("_").append(shardingIndex);
						/**
						 * 分片字段，必须放在where子句中
						 */
						WHERE("`" + fc.getColumnName() + "` = #{criteria.shardingVal} ");
					}
				}
			}

			WHERE(criteria.toSql());
			DELETE_FROM(tableNameBuilder.toString());
		}}.toString();
	}
}
