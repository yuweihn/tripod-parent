package com.yuweix.tripod.dao.hibernate;


import org.hibernate.query.NativeQuery;
import org.springframework.orm.hibernate5.HibernateCallback;

import java.util.Collection;
import java.util.Map;


/**
 * @author yuwei
 */
public abstract class AbstractCallback<T> implements HibernateCallback<T> {
	protected void assembleParams(NativeQuery<?> query, Map<String, Object> params) {
		if (params == null || params.size() <= 0) {
			return;
		}

		for (String key : params.keySet()) {
			Object value = params.get(key);
			if (value instanceof Collection<?>) {
				query.setParameterList(key, (Collection<?>) value);
			} else {
				query.setParameter(key, value);
			}
		}
	}

	protected void assembleParams(NativeQuery<?> query, Object[] params) {
		if (params == null || params.length <= 0) {
			return;
		}
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i + 1, params[i]);
		}
	}
}
