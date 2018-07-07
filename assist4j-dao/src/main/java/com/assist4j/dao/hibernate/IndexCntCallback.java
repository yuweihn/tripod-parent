package com.assist4j.dao.hibernate;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;


/**
 * @author wei
 */
public class IndexCntCallback extends IndexParamCallback<Integer> {
	protected String sql;
	protected Object[] params;

	public IndexCntCallback(String sql, Object[] params) {
		this.sql = sql;
		this.params = params;
	}

	@Override
	public Object doInHibernate(Session session) throws HibernateException {
		NativeQuery<Integer> query = session.createNativeQuery(sql, Integer.class);
		assembleParams(query, params);
		return new Integer(query.uniqueResult().toString());
	}
}
