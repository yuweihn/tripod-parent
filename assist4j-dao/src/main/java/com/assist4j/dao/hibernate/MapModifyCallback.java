package com.assist4j.dao.hibernate;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;


/**
 * @author wei
 */
public class MapModifyCallback extends MapParamCallback<Integer> {
	protected String sql;
	protected Map<String, Object> params;

	public MapModifyCallback(String sql, Map<String, Object> params) {
		this.sql = sql;
		this.params = params;
	}

	@Override
	public List<Integer> doInHibernate(Session session) throws HibernateException {
		NativeQuery<Integer> query = session.createNativeQuery(sql);
		assembleParams(query, params);
		int count = query.executeUpdate();

		List<Integer> list = new ArrayList<Integer>();
		list.add(count);
		return list;
	}
}
