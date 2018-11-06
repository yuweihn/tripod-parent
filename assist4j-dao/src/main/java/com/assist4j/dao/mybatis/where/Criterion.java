package com.assist4j.dao.mybatis.where;


import java.util.Map;
import java.util.UUID;


/**
 * @author yuwei
 */
class Criterion {
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
}
