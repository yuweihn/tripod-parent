package com.yuweix.tripod.dao.hibernate;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;


/**
 * @author yuwei
 */
public class IndexModifyCallback extends IndexParamCallback {
	protected String sql;
	protected Object[] params;

	public IndexModifyCallback(String sql, Object[] params) {
		this.sql = sql;
		this.params = params;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object doInHibernate(Session session) throws HibernateException {
		NativeQuery<Object> query = session.createNativeQuery(sql);
		assembleParams(query, params);
		return query.executeUpdate();
	}
}
