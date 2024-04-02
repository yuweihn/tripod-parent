package com.yuweix.tripod.dao.sharding;


/**
 * @author yuwei
 **/
public class DatabaseConfig {
	/**
	 * 逻辑库后占位符长度
	 * eg.
	 * gateway  ====>>>>  gateway_0001
	 * @return   逻辑库后占位符长度
	 */
	private int suffixLength = 4;
	/**
	 * 分库数量
	 */
	private int shardingSize = 2;

	public int getSuffixLength() {
		return suffixLength;
	}

	public void setSuffixLength(int suffixLength) {
		this.suffixLength = suffixLength;
	}

	public int getShardingSize() {
		return shardingSize;
	}

	public void setShardingSize(int shardingSize) {
		this.shardingSize = shardingSize;
	}
}
