package com.yuweix.assist4j.dao.hibernate;


import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.query.NativeQuery;


/**
 * @author wei
 */
public class MapCallback extends MapParamCallback {
	protected String sql;
	protected Class<?> clz;
	protected Map<String, Object> params;
	protected Integer pageNo;
	protected Integer pageSize;

	public MapCallback(String sql, Class<?> clz, Map<String, Object> params) {
		this.sql = sql;
		this.clz = clz;
		this.params = params;
	}

	public MapCallback(String sql, Class<?> clz, int pageNo, int pageSize, Map<String, Object> params) {
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
