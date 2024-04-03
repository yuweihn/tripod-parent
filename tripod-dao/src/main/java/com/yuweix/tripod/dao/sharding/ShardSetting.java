package com.yuweix.tripod.dao.sharding;


/**
 * @author yuwei
 **/
public interface ShardSetting {
	/**
	 * 逻辑库/表后占位符长度
	 * eg.
	 * user  ====>>>>  user_0000
	 * @return   逻辑库/表后占位符长度
	 */
	int getSuffixLength();

	/**
	 * 分库/表数量
	 */
	int getShardingSize();
}
