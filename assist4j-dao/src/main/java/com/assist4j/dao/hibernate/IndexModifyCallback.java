package com.assist4j.dao.hibernate;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;


/**
 * @author wei
 */
public class IndexModifyCallback extends IndexParamCallback<Object> {
	protected String sql;
	protected Object[] params;

	public IndexModifyCallback(String sql, Object[] params) {
		this.sql = sql;
		this.params = params;
	}

	@Override
	public Object doInHibernate(Session session) throws HibernateException {
		NativeQuery<Object> query = session.createNativeQuery(sql);
		assembleParams(query, params);
		return query.executeUpdate();
	}
}
