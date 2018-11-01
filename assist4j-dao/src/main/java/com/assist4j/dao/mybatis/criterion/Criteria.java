package com.assist4j.dao.mybatis.criterion;


import java.io.Serializable;


/**
 * @author yuwei
 */
public class Criteria implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * key为表的字段，不是对象的属性
	 */
	private String key;
	private Operator operator;
	private Object value;

	private Criteria and;
	private Criteria or;

	public Criteria(String key, Operator operator, Object value) {
		this.key = key;
		this.operator = operator;
		this.value = value;
	}

	public Criteria and(Criteria and) {
		this.and = and;
		return this;
	}
	public Criteria or(Criteria or) {
		this.or = or;
		return this;
	}



	/////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
