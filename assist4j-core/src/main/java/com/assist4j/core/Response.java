package com.assist4j.core;


import com.alibaba.fastjson.JSONObject;
import java.io.Serializable;


/**
 * @author yuwei
 */
public class Response<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String msg;
	private T data;

	public Response() {

	}
	public Response(String code, String msg) {
		this(code, msg, null);
	}
	public Response(String code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}


	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
}
