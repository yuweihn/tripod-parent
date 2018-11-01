package com.assist4j.dao.mybatis.where;


import java.io.Serializable;


/**
 * @author yuwei
 */
public class Criterion implements Serializable {
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

	public Criterion(String key, Operator operator) {
		this(key, operator, null);
	}
	public Criterion(String key, Operator operator, Object value) {
		this.key = key;
		this.operator = operator;
		this.value = value;
	}

	@Override
	public String toString() {
		if (key == null || "".equals(key) || operator == null) {
			return null;
		}

		if (value == null) {
			return key + " " + operator.getCode() + " ";
		} else if (value instanceof Number) {
				return key + " " + operator.getCode() + " " + value + " ";
		} else {
			return key + " " + operator.getCode() + " '" + value + "' ";
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void setKey(String key) {
		this.key = key;
	}
	public String getKey() {
		return key;
	}
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	public Operator getOperator() {
		return operator;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Object getValue() {
		return value;
	}
}
