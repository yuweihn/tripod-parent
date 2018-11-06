package com.assist4j.dao.mybatis.order;


import java.util.Map;
import java.util.UUID;


/**
 * @author yuwei
 */
public class ColumnOrderedPair{
	private String columnParamKey;

	private Ordered ordered;

	public ColumnOrderedPair(String column, Ordered ordered, Map<String, Object> params) {
		this.columnParamKey = column + UUID.randomUUID().toString().replace("-", "");
		params.put(this.columnParamKey, column);

		this.ordered = ordered;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public String toSql() {
		if (this.ordered == null) {
			return " #{orderBy.params." + this.columnParamKey + "} ";
		} else {
			return " #{orderBy.params." + this.columnParamKey + "} " + ordered.getCode();
		}
	}
}
