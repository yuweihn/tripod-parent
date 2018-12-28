package com.assist4j.session.filter;




/**
 * @author yuwei
 */
public class ParamHolder {
	private PathPattern exclusivePattern;
	private ValueSplit valueSplit;


	private ParamHolder() {

	}
	private static class Holder {
		private static final ParamHolder instance = new ParamHolder();
	}

	public static ParamHolder getInstance() {
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
