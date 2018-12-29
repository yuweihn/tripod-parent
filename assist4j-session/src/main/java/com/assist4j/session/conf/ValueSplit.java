package com.assist4j.session.conf;




/**
 * 当value值过长时，将其拆为多个值分开存储。
 * @author yuwei
 */
public class ValueSplit {
	private static final int DEFAULT_MAX_LENGTH = 1024;

	/**
	 * 是否分拆value值
	 */
	private boolean flag;
	/**
	 * 分拆value值时每个子串的最大长度
	 */
	private int maxLength;

	public ValueSplit() {
		this(false);
	}
	public ValueSplit(boolean flag) {
		this(flag, DEFAULT_MAX_LENGTH);
	}
	public ValueSplit(boolean flag, int maxLength) {
		this.flag = flag;
		this.maxLength = maxLength;
	}



	public boolean isFlag() {
		return flag;
	}

	public boolean getFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
}
