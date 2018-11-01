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
		}
	}

	public static Criteria create(Criterion criterion) {
		return new Criteria(criterion);
	}

	public Criteria add(Connector connector, Criterion criterion) {
		return this;
	}

	public Criteria add(Connector connector, Criteria criteria) {
		return this;
	}

	public String toSql() {
		if (criterion == null) {
			return "";
		}

		String thisSql = criterion.toSql();
		if (thisSql == null) {
			return "";
		}

		if (connector == null || another == null) {
			return thisSql;
		}

		StringBuilder builder = new StringBuilder("");
		builder.append(thisSql).append(" ")
				.append(connector.getCode()).append(" ")
				.append(" (").append(another.toSql()).append(") ");

		return builder.toString();
	}
}

