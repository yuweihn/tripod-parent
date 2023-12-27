package com.yuweix.tripod.dao.hibernate;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;

import java.util.Map;


/**
 * @author yuwei
 */
public class IndexCallback extends IndexParamCallback {
	protected String sql;
	protected Class<?> clz;
	protected Object[] params;
	protected Integer pageNo;
	protected Integer pageSize;

	public IndexCallback(String sql, Class<?> clz, Object[] params) {
		this.sql = sql;
		this.clz = clz;
		this.params = params;
	}

	public IndexCallback(String sql, Class<?> clz, int pageNo, int pageSize, Object[] params) {
		this.sql = sql;
		this.clz = clz;
		this.params = params;
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Object doInHibernate(Session session) throws HibernateException {
		NativeQuery<?> query = null;
		if (clz != null && Map.class.isAssignableFrom(clz)) {
			query = session.createNativeQuery(sql);
			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		} else {
			query = session.createNativeQuery(sql, clz);
		}
		assembleParams(query, params);

		if (pageNo != null && pageSize != null) {
			if (pageNo <= 0) {
				pageNo = 1;
			}
			if (pageSize <= 0) {
				pageSize = DEFAULT_PAGE_SIZE;
			}
			query.setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize);
		}

		return query.list();
	}
}
