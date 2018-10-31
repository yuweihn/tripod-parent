package com.assist4j.dao.mybatis.where;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yuwei
 */
public class Where implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private List<KeyVal> kvList;
	private Integer pageNo;
	private Integer pageSize;
	private String orderBy;

	private Where() {

	}
	public static Where create() {
		return new Where();
	}

	public Where add(String key, Operator operator, Object val) {
		if (kvList == null) {
			kvList = new ArrayList<KeyVal>();
		}
		kvList.add(new KeyVal(key, operator, val));
		return this;
	}




	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public Where setKvList(List<KeyVal> kvList) {
		this.kvList = kvList;
		return this;
	}

	public List<KeyVal> getKvList() {
		return kvList;
	}

	public Where setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
		return this;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public Where setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Where setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	public String getOrderBy() {
		return orderBy;
	}
}
