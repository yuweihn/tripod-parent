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
}
