package com.assist4j.dao.mybatis.criterion;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author yuwei
 */
public class Criteria implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private List<KeyVal> kvList;
	private Integer pageNo;
	private Integer pageSize;
	private String orderBy;

	private Criteria() {

	}
	public static Criteria create() {
		return new Criteria();
	}

	public Criteria add(String key, Operator operator, Object val) {
		if (kvList == null) {
			kvList = new ArrayList<KeyVal>();
		}
		kvList.add(new KeyVal(key, operator, val));
		return this;
	}




	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public List<KeyVal> getKvList() {
		return kvList;
	}

	public Criteria setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
		return this;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public Criteria setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Criteria setOrderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	public String getOrderBy() {
		return orderBy;
	}
}
