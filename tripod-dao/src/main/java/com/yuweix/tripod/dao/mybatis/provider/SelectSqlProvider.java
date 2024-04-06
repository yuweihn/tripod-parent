package com.yuweix.tripod.dao.mybatis.provider;


import com.yuweix.tripod.dao.PersistUtil;
import com.yuweix.tripod.dao.mybatis.order.OrderBy;
import com.yuweix.tripod.dao.mybatis.where.Criteria;
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
//		PK id = (PK) param.get("id");
		Class<T> entityClass = (Class<T>) param.get("clz");
		String tbName = PersistUtil.getTableName(entityClass);
		
		List<PersistUtil.FieldCol> fcList = PersistUtil.getPersistFieldList(entityClass);
		return new SQL() {{
			boolean whereSet = false;
			for (PersistUtil.FieldCol fc: fcList) {
				Field field = fc.getField();

				SELECT(fc.getColumnName() + " as " + field.getName());
				
				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					WHERE(fc.getColumnName() + " = #{id}");
					whereSet = true;
				}
			}
			if (!whereSet) {
				throw new IllegalAccessException("'where' is required.");
			}
			FROM(tbName);
		}}.toString();
	}

	@SuppressWarnings("unchecked")
	public <T>String findCount(Map<String, Object> param) throws IllegalAccessException {
		Criteria criteria = (Criteria) param.get("criteria");
		Class<T> entityClass = (Class<T>) param.get("clazz");
		String tbName = PersistUtil.getTableName(entityClass);

		StringBuilder builder = new StringBuilder("");
		builder.append("<script>");
		builder.append(" select count(1) as cnt ");
		builder.append(" from ").append(tbName).append("  ");
		builder.append(" where 1 = 1 ");
		if (criteria != null) {
			String criteriaSql = criteria.toSql();
			if (criteriaSql != null && !"".equals(criteriaSql.trim())) {
				builder.append(" and ").append(criteriaSql).append(" ");
			}
		}
		builder.append("</script>");
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	public <T>String findList(Map<String, Object> param) throws IllegalAccessException {
		Criteria criteria = (Criteria) param.get("criteria");
		OrderBy orderBy = (OrderBy) param.get("orderBy");
		Class<T> entityClass = (Class<T>) param.get("clazz");
		Integer pageNo0 = null;
		if (param.containsKey("pageNo")) {
			pageNo0 = (Integer) param.get("pageNo");
		}
		Integer pageSize0 = null;
		if (param.containsKey("pageSize")) {
			pageSize0 = (Integer) param.get("pageSize");
		}
		String tbName = PersistUtil.getTableName(entityClass);

		StringBuilder builder = new StringBuilder("");
		builder.append("<script>");
		builder.append(" select ").append(PersistUtil.getAllColumnSql(entityClass));
		builder.append(" from ").append(tbName).append("  ");
		builder.append(" where 1 = 1 ");
		if (criteria != null) {
			String criteriaSql = criteria.toSql();
			if (criteriaSql != null && !"".equals(criteriaSql.trim())) {
				builder.append(" and ").append(criteriaSql).append(" ");
			}
		}
		if (orderBy != null) {
			String orderBySql = orderBy.toSql();
			if (orderBySql != null && !"".equals(orderBySql.trim())) {
				builder.append(" order by ").append(orderBySql.trim()).append(" ");
			}
		}
		if (pageNo0 != null && pageSize0 != null) {
			int pageNo = pageNo0 <= 0 ? 1 : pageNo0;
			int pageSize = pageSize0 <= 0 ? 10 : pageSize0;
			builder.append(" limit ").append((pageNo - 1) * pageSize).append(", ").append(pageSize);
		}
		builder.append("</script>");
		return builder.toString();
	}
}

