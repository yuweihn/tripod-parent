package com.yuweix.tripod.dao.hibernate;


import org.springframework.orm.hibernate5.HibernateCallback;


/**
 * @author yuwei
 */
public abstract class AbstractParamCallback implements HibernateCallback<Object> {
	protected static final int DEFAULT_PAGE_SIZE = 10;
}
