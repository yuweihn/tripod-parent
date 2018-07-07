package com.assist4j.dao.hibernate;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
//import org.hibernate.transform.Transformers;

import java.util.List;
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
	public List<T> doInHibernate(Session session) throws HibernateException {
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

		query.
		List<T> list = query.list();
		return list;
	}
}
