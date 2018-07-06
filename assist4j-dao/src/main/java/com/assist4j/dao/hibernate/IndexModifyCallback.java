package com.assist4j.dao.hibernate;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import java.util.ArrayList;
import java.util.List;


/**
 * @author wei
 */
public class IndexModifyCallback extends IndexParamCallback<Integer> {
	protected String sql;
	protected Object[] params;

	public IndexModifyCallback(String sql, Object[] params) {
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
