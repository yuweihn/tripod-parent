package com.assist4j.dao.mybatis.criterion;




/**
 * 连接符
 * @author yuwei
 */
public enum Connector {
	and(" and "),
	or(" or ");

	private String code;

	Connector(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
