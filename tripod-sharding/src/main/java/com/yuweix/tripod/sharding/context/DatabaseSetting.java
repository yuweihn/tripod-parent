package com.yuweix.tripod.sharding.context;


/**
 * @author yuwei
 **/
public interface DatabaseSetting {
	/**
	 * 逻辑库后占位符长度
	 * eg.
	 * gateway  ====>>>>  gateway_0000
	 * @return   逻辑库后占位符长度
	 */
	int getSuffixLength();

	/**
	 * 逻辑库名与分片索引之间的分隔符
	 */
	String getSplit();
}
