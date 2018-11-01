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

	private Criterion first;
	private Connector connector;
	private Criterion second;

	private Criteria() {

	}

	public static Criteria create() {
		return new Criteria();
	}



	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public Criteria setFirst(Criterion first) {
		this.first = first;
		return this;
	}
	public Criterion getFirst() {
		return first;
	}
	public Criteria setConnector(Connector connector) {
		this.connector = connector;
		return this;
	}
	public Connector getConnector() {
		return connector;
	}
	public Criteria setSecond(Criterion second) {
		this.second = second;
		return this;
	}
	public Criterion getSecond() {
		return second;
	}
}
