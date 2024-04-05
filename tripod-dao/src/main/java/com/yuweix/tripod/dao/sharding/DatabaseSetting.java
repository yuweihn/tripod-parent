package com.yuweix.tripod.dao.sharding;


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
}
