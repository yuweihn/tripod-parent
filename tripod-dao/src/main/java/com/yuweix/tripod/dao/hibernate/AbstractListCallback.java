package com.yuweix.tripod.dao.hibernate;


import java.util.List;


/**
 * @author yuwei
 */
public abstract class AbstractListCallback<T> extends AbstractCallback<List<T>> {
	protected static final int DEFAULT_PAGE_SIZE = 10;
}
