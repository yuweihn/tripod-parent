package com.assist4j.dao.hibernate;


import org.hibernate.query.NativeQuery;


/**
 * @author wei
 */
public abstract class IndexParamCallback<T> extends AbstractParamCallback {
	protected void assembleParams(NativeQuery<T> query, Object[] params) {
		if (params == null || params.length <= 0) {
			return;
		}

		for (int i = 0; i < params.length; i++) {
			query.setParameter(i + 1, params[i]);
		}
	}
}
