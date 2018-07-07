package com.assist4j.dao.hibernate;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.query.NativeQuery;

import java.util.Map;


/**
 * @author wei
 */
public class IndexCallback<T> extends IndexParamCallback<T> {
	protected String sql;
	protected Class<T> clz;
	protected Object[] params;
	protected Integer pageNo;
	protected Integer pageSize;

	public IndexCallback(String sql, Class<T> clz, Object[] params) {
		this.sql = sql;
		this.clz = clz;
		this.params = params;
	}

	public IndexCallback(String sql, Class<T> clz, int pageNo, int pageSize, Object[] params) {
		this.sql = sql;
		this.clz = clz;
		this.params = params;
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}

	@Override
	public Object doInHibernate(Session session) throws HibernateException {
		NativeQuery<T> query = null;
		if (clz != null && Map.class.isAssignableFrom(clz)) {
			query = session.createNativeQuery(sql);
			query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP);
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
