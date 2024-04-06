package com.yuweix.tripod.dao.mybatis.provider;


import com.yuweix.tripod.dao.PersistUtil;
import com.yuweix.tripod.dao.mybatis.where.Criteria;
import org.apache.ibatis.jdbc.SQL;

import javax.persistence.Id;
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

		List<PersistUtil.FieldCol> fcList = PersistUtil.getPersistFieldList(entityClass);
		return new SQL() {{
			boolean whereSet = false;
			for (PersistUtil.FieldCol fc: fcList) {
				Field field = fc.getField();
				Id idAnn = field.getAnnotation(Id.class);

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
			DELETE_FROM(tbName);
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <PK, T>String deleteByKey(Map<String, Object> param) throws IllegalAccessException {
//		PK id = (PK) param.get("id");
		Class<T> entityClass = (Class<T>) param.get("clz");
		String tbName = PersistUtil.getTableName(entityClass);

		List<PersistUtil.FieldCol> fcList = PersistUtil.getPersistFieldList(entityClass);
		return new SQL() {{
			boolean whereSet = false;
			for (PersistUtil.FieldCol fc: fcList) {
				Field field = fc.getField();
				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					WHERE("`" + fc.getColumnName() + "` = #{id}");
					whereSet = true;
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is required.");
			}
			DELETE_FROM(tbName);
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <T>String deleteByCriteria(Map<String, Object> param) throws IllegalAccessException {
		Class<T> entityClass = (Class<T>) param.get("clz");
		String tbName = PersistUtil.getTableName(entityClass);
		Criteria criteria = (Criteria) param.get("criteria");
		if (criteria == null || criteria.getParams() == null || criteria.getParams().size() <= 0) {
			throw new IllegalAccessException("'where' is required.");
		}

		return new SQL() {{
			WHERE(criteria.toSql());
			DELETE_FROM(tbName);
		}}.toString();
	}
}
