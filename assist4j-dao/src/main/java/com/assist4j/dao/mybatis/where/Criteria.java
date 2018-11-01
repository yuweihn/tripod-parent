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

	private Criterion criterion;
	private Criteria another;
	private Connector connector;

	private Criteria() {

	}

	public static Criteria create() {
		return new Criteria();
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


	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public Criteria setCriterion(Criterion criterion) {
		this.criterion = criterion;
		return this;
	}
	public Criterion getCriterion() {
		return criterion;
	}
	public Criteria setConnector(Connector connector) {
		this.connector = connector;
		return this;
	}
	public Connector getConnector() {
		return connector;
	}
	public Criteria setAnother(Criteria another) {
		this.another = another;
		return this;
	}
	public Criteria getAnother() {
		return another;
	}
}

