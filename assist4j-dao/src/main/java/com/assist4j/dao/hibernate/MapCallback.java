package com.assist4j.dao.hibernate;


import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;


/**
 * @author wei
 */
public class MapCallback<T> extends MapParamCallback<T> {
	protected String sql;
	protected Class<T> clz;
	protected Map<String, Object> params;
	protected Integer pageNo;
	protected Integer pageSize;

	public MapCallback(String sql, Class<T> clz, Map<String, Object> params) {
		this.sql = sql;
		this.clz = clz;
		this.params = params;
	}

	public MapCallback(String sql, Class<T> clz, int pageNo, int pageSize, Map<String, Object> params) {
		this.sql = sql;
		this.clz = clz;
		this.params = params;
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}

	@Override
	public Object doInHibernate(Session session) throws HibernateException {
		NativeQuery<T> query = session.createNativeQuery(sql, clz);
		assembleParams(query, params);

		if (clz == null) {
			
		} else if (Number.class.isAssignableFrom(clz)) {
			
//		} else if (Map.class.isAssignableFrom(clz)) {
////			query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		} else {
			query.addEntity(clz);
		}

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
