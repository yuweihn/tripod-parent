package com.assist4j.data.cache;


/**
 * @author yuwei
 * 存入缓存的数据格式如下：
 * {"className": "", "data": ""}
 * 如：
 * {"className": "java.lang.String", "data": "测试数据"}
 * {"className": "com.assist4j.session.SessionAttribute","data": "{\"lastAccessTime\":1481378170858,\"createTime\":1481378170856,\"attribute\":[\"{\\\"value\\\":\\\"2\\\",\\\"key\\\":\\\"sessionUserId\\\",\\\"valueClassName\\\":\\\"java.lang.Long\\\"}\"],\"newBuild\":true}"}
 */
public class ValueData {
	private String data;
	private String className;
	
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
}
