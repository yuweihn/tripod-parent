package com.yuweix.assist4j.dao.hibernate;


import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;


/**
 * @author wei
 */
public class MapModifyCallback extends MapParamCallback {
	protected String sql;
	protected Map<String, Object> params;

	public MapModifyCallback(String sql, Map<String, Object> params) {
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
