package com.assist4j.dao.mybatis.where;


import java.io.Serializable;
import java.util.Map;
import java.util.UUID;


/**
 * @author yuwei
 */
class Criterion implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	private Map<String, Object> params = null;
	private String paramKey;

	/**
	 * key为表的字段，不是对象的属性
	 */
	private String key;
	private Operator operator;
	private Object value;

	public Criterion(String key, Operator operator, Object value, Map<String, Object> params) {
		this.key = key;
		this.operator = operator;
		this.value = value;

		if (this.value != null) {
			this.paramKey = key + Md5Util.getMd5(operator.getCode()) + UUID.randomUUID().toString().replace("-", "");
			params.put(this.paramKey, this.value);
		}
		this.params = params;
	}

	public String toSql() {
		if (key == null || "".equals(key.trim()) || operator == null) {
			return null;
		}
		if (value == null) {
			return key + " " + operator.getCode() + " ";
		}

		return key + " " + operator.getCode() + " #{criteria.params." + this.paramKey + "} ";
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
