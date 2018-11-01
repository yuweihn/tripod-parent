package com.assist4j.dao.mybatis.where;


import java.io.Serializable;


/**
 * @author yuwei
 */
public class Criteria implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private StringBuilder sql = new StringBuilder("");

	private Criteria(Criterion criterion) {
		String criterionSql = criterion.toSql();
		if (criterionSql != null && !"".equals(criterionSql.trim())) {
			sql.append(criterionSql);
		} else {
			throw new RuntimeException("Invalid criterion.");
		}
	}

	public static Criteria create(Criterion criterion) {
		return new Criteria(criterion);
	}

	public Criteria add(Connector connector, Criterion criterion) {
		String criterionSql = criterion.toSql();
		if (criterionSql != null && !"".equals(criterionSql.trim())) {
			sql.append(" ").append(connector.getCode()).append(" ").append(criterionSql).append(" ");
		}
		return this;
	}

	public Criteria add(Connector connector, Criteria criteria) {
		String criteriaSql = criteria.toSql();
		if (criteriaSql != null && !"".equals(criteriaSql.trim())) {
			sql.insert(0, "(").append(") ").append(connector.getCode()).append(" (").append(criteriaSql).append(") ");
		}
		return this;
	}

	public String toSql() {
		return sql.toString();
	}
}

