package com.yuweix.tripod.core;


import com.yuweix.tripod.core.json.JsonUtil;
import java.io.Serializable;


/**
 * @author yuwei
 */
public class Response<C, D> implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private C code;
	private String msg;
	private D data;

	public Response() {

	}
	public Response(C code, String msg) {
		this(code, msg, null);
	}
	public Response(C code, String msg, D data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}
	public static<CC, DD> Response<CC, DD> of(CC code, String msg) {
		return of(code, msg, null);
	}
	public static<CC, DD> Response<CC, DD> of(CC code, String msg, DD data) {
		return new Response<>(code, msg, data);
	}

	@Override
	public String toString() {
		return JsonUtil.toJSONString(this);
	}


	public C getCode() {
		return code;
	}
	public void setCode(C code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public D getData() {
		return data;
	}
	public void setData(D data) {
		this.data = data;
	}
}
