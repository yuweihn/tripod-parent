package com.yuweix.tripod.dao.hibernate;


import org.hibernate.query.NativeQuery;


/**
 * @author yuwei
 */
public abstract class IndexParamCallback extends AbstractParamCallback {
	protected void assembleParams(NativeQuery<?> query, Object[] params) {
		if (params == null || params.length <= 0) {
			return;
		}

		for (int i = 0; i < params.length; i++) {
			query.setParameter(i + 1, params[i]);
		}
	}
}
