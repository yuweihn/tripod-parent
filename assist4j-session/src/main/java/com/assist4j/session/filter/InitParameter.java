package com.assist4j.session.filter;




/**
 * @author yuwei
 */
public class InitParameter {
	private PathPattern exclusivePattern;
	private ValueSplit valueSplit;


	private InitParameter() {

	}
	private static class Holder {
		private static final InitParameter instance = new InitParameter();
	}

	public static InitParameter getInstance() {
		return Holder.instance;
	}

	public void setExclusivePattern(String[] exclusiveURLs) {
		exclusivePattern = new PathPattern(exclusiveURLs);
	}
	public PathPattern getExclusivePattern() {
		return exclusivePattern;
	}

	public void setValueSplit(ValueSplit valueSplit) {
		this.valueSplit = valueSplit;
	}

	public ValueSplit getValueSplit() {
		return valueSplit;
	}
}
