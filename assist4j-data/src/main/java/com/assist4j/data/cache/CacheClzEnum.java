package com.assist4j.data.cache;


/**
 * 允许存入缓存的数据类型
 * @author yuwei
 */
public enum CacheClzEnum {
	Number(Number.class),
	String(String.class),
	CacheValue(CacheValue.class);


	private Class<?> clz;

	CacheClzEnum(Class<?> clz) {
		this.clz = clz;
	}

	public Class<?> getClz() {
		return clz;
	}
}
