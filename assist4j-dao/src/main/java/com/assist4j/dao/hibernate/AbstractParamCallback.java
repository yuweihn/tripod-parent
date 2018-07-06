package com.assist4j.dao.hibernate;


import org.springframework.orm.hibernate5.HibernateCallback;
import java.util.List;


/**
 * @author wei
 */
public abstract class AbstractParamCallback<T> implements HibernateCallback<List<T>> {
	protected static final int DEFAULT_PAGE_SIZE = 10;
}
