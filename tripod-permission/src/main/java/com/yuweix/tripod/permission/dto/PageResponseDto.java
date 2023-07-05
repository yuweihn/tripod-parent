package com.yuweix.tripod.permission.dto;


import java.io.Serializable;
import java.util.List;


/**
 * @author yuwei
 */
public class PageResponseDto<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 数据条数
	 */
	private Integer size;

	/**
	 * 数据项
	 */
	private List<T> list;


	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
}
