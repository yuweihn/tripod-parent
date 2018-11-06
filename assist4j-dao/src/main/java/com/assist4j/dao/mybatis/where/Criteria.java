package com.assist4j.dao.mybatis.where;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * @author yuwei
 */
public class Criteria implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private StringBuilder sql = new StringBuilder("");
	private Map<String, Object> params = new HashMap<String, Object>();

	private Criteria() {

	}

	public static Criteria create(String key, Operator operator) {
		return create(key, operator, null);
	}
	public static Criteria create(String key, Operator operator, Object value) {
		Criteria criteria = new Criteria();
		Criterion criterion = new Criterion(key, operator, value, criteria.params);
		String criterionSql = criterion.toSql();
		if (criterionSql != null && !"".equals(criterionSql.trim())) {
			criteria.sql.append(criterionSql);
		} else {
			throw new RuntimeException("Invalid criterion.");
		}

		return criteria;
	}

	private Criteria add(Connector connector, Criterion criterion) {
		String criterionSql = criterion.toSql();
		if (criterionSql != null && !"".equals(criterionSql.trim())) {
			sql.append(" ")
				.append(connector.getCode())
				.append(" ")
				.append(criterionSql)
				.append(" ");
		}
		return this;
	}
	public Criteria and(String key, Operator operator) {
		return and(key, operator, null);
	}
	public Criteria and(String key, Operator operator, Object value) {
		Criterion criterion = new Criterion(key, operator, value, this.params);
		return add(Connector.and, criterion);
	}
	public Criteria or(String key, Operator operator) {
		return or(key, operator, null);
	}
	public Criteria or(String key, Operator operator, Object value) {
		Criterion criterion = new Criterion(key, operator, value, this.params);
		return add(Connector.or, criterion);
	}

	private Criteria add(Connector connector, Criteria criteria) {
		this.putAllParams(criteria.getParams());
		String criteriaSql = criteria.toSql();
		if (criteriaSql != null && !"".equals(criteriaSql.trim())) {
			sql.insert(0, "(")
				.append(") ")
				.append(connector.getCode())
				.append(" (")
				.append(criteriaSql)
				.append(") ");
		}
		return this;
	}
	public Criteria and(Criteria criteria) {
		return add(Connector.and, criteria);
	}
	public Criteria or(Criteria criteria) {
		return add(Connector.or, criteria);
	}

	public String toSql() {
		return sql.toString();
	}

	public void putAllParams(Map<String, Object> params) {
		this.params.putAll(params);
	}
	public Map<String, Object> getParams() {
		return params;
	}
}

