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

	private List<Criterion> criterionList;

	private Criteria() {

	}
	public static Criteria create() {
		return new Criteria();
	}

	public Criteria add(String key, Operator operator, Object val) {
		if (criterionList == null) {
			criterionList = new ArrayList<Criterion>();
		}
		criterionList.add(new Criterion(key, operator, val));
		return this;
	}




	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public List<Criterion> getCriterionList() {
		return criterionList;
	}
}
