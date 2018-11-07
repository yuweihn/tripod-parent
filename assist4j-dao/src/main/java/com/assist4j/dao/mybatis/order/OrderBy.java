package com.assist4j.dao.mybatis.order;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author yuwei
 */
public class OrderBy implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private List<ColumnOrderedPair> columnOrderedPairList;
	private Map<String, Object> params;


	private OrderBy() {
		columnOrderedPairList = new ArrayList<ColumnOrderedPair>();
		params = new HashMap<String, Object>();
	}

	public static OrderBy create() {
		return new OrderBy();
	}
	public static OrderBy create(String column) {
		return create().add(column);
	}
	public static OrderBy create(String column, Ordered ordered) {
		return create().add(column, ordered);
	}

	public OrderBy add(String column) {
		return add(column, null);
	}
	public OrderBy add(String column, Ordered ordered) {
		columnOrderedPairList.add(new ColumnOrderedPair(column, ordered, params));
		return this;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public List<ColumnOrderedPair> getColumnOrderedPairList() {
		return columnOrderedPairList;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public String toSql() {
		if (columnOrderedPairList == null || columnOrderedPairList.size() <= 0) {
			return null;
		}

		StringBuilder builder = new StringBuilder("");
		for (int i = 0, size = columnOrderedPairList.size(); i < size; i++) {
			ColumnOrderedPair cop = columnOrderedPairList.get(i);
			if (i > 0) {
				builder.append(", ");
			}

			builder.append(cop.toSql());
		}
		return builder.toString();
	}
}
