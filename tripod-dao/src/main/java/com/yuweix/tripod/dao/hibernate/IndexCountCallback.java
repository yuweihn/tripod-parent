package com.yuweix.tripod.dao.hibernate;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;


/**
 * @author yuwei
 */
public class IndexCountCallback extends AbstractIntegerCallback {
	protected String sql;
	protected Object[] params;

	public IndexCountCallback(String sql, Object[] params) {
		this.sql = sql;
		this.params = params;
	}

	@Override
	public Integer doInHibernate(Session session) throws HibernateException {
		NativeQuery<Integer> query = session.createNativeQuery(sql, Integer.class);
		assembleParams(query, params);
		return Integer.parseInt(query.uniqueResult().toString());
	}
}
