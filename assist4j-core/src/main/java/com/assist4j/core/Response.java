package com.assist4j.core;


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
		this(CODE_FAILURE, "");
	}
	public Response(String code, String msg) {
		this(code, msg, null);
	}
	public Response(String code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	/**
	 * 操作成功
	 */
	public static final String CODE_SUCCESS = "0000";
	/**
	 * 操作失败
	 */
	public static final String CODE_FAILURE = "9999";

	public String toJson() {
		return JsonUtil.toJson(this);
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
