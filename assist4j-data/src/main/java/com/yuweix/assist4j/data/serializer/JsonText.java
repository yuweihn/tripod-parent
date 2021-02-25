package com.yuweix.assist4j.data.serializer;



/**
 * @author yuwei
 */
public class JsonText {
	private String clz;
	private Object text;

	public JsonText() {

	}
	public JsonText(String clz, Object text) {
		this.clz = clz;
		this.text = text;
	}


	public void setClz(String clz) {
		this.clz = clz;
	}

	public String getClz() {
		return clz;
	}

	public void setText(Object text) {
		this.text = text;
	}

	public Object getText() {
		return text;
	}
}
