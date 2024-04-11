package com.yuweix.tripod.dao.hibernate;


import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.yuweix.tripod.dao.sharding.Shard;
import org.hibernate.Session;
import org.hibernate.SessionFactory;


/**
 * @author yuwei
 */
public abstract class AbstractDao<T extends Serializable, PK extends Serializable> implements Dao<T, PK> {
	protected Class<T> clz;
	protected SessionFactory sessionFactory;
	protected ThreadLocal<Session> sessionTL = new ThreadLocal<>();


	@SuppressWarnings("unchecked")
	public AbstractDao() {
		this.clz = null;
		Type t = getClass().getGenericSuperclass();
		if (t instanceof ParameterizedType) {
			this.clz = (Class<T>) ((ParameterizedType) t).getActualTypeArguments()[0];
		}
	}

	@Resource
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		Session sess = sessionTL.get();
		return sess != null ? sess : sessionFactory.getCurrentSession();
	}

	@Override
	public void onStart() {
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		sessionTL.set(session);
	}
	@Override
	public void onSuccess() {
		Session session = sessionTL.get();
		if (session == null) {
			return;
		}
		session.getTransaction().commit();
	}
	@Override
	public void onFailure() {
		Session session = sessionTL.get();
		if (session == null) {
			return;
		}
		session.getTransaction().rollback();
	}
	@Override
	public void onComplete() {
		Session session = sessionTL.get();
		if (session != null && session.isOpen()) {
			session.close();
		}
		sessionTL.remove();
	}

	@Override
	public Class<?> getPersistClz() {
		return clz;
	}

	@Override
	public T get(final PK id) {
		return getSession().get(clz, id);
	}

	@Override
	public T get(@Shard Object shardingVal, PK id) {
		return getSession().get(clz, id);
	}

	@Override
	public void save(@Shard final T t) {
		getSession().save(t);
	}

	@Override
	public void update(@Shard final T t) {
		getSession().update(t);
	}

	@Override
	public void deleteByKey(PK id) {
		final T t = get(id);
		if (t != null) {
			getSession().delete(t);
		}
	}

	@Override
	public void deleteByKey(@Shard Object shardingVal, PK id) {
		final T t = get(id);
		if (t != null) {
			getSession().delete(t);
		}
	}

	@Override
	public void delete(@Shard final T t) {
		getSession().delete(t);
	}


	/**
	 * 查询某个表的所有字段
	 * @param sql
	 * @param params
	 * @return
	 */
	protected List<T> query(String sql, Map<String, Object> params) {
		return new MapCallback<>(sql, clz, params).doInHibernate(getSession());
	}
	/**
	 * 查询某个表的所有字段
	 * @param sql
	 * @return
	 */
	protected List<T> query(String sql) {
		return query(sql, (Object[]) null);
	}
	/**
	 * 查询某个表的所有字段
	 * @param sql
	 * @param params
	 * @return
	 */
	protected List<T> query(String sql, Object[] params) {
		return new IndexCallback<>(sql, clz, params).doInHibernate(getSession());
	}


	/**
	 * 查询某个表的所有字段。分页查询
	 * @param sql
	 * @param pageNo
	 * @param pageSize
	 * @param params
	 * @return
	 */
	protected List<T> query(String sql, int pageNo, int pageSize, Map<String, Object> params) {
		return new MapCallback<>(sql, clz, pageNo, pageSize, params).doInHibernate(getSession());
	}
	/**
	 * 查询某个表的所有字段。分页查询
	 * @param sql
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	protected List<T> query(String sql, int pageNo, int pageSize) {
		return query(sql, pageNo, pageSize, (Object[]) null);
	}
	/**
	 * 查询某个表的所有字段。分页查询
	 * @param sql
	 * @param pageNo
	 * @param pageSize
	 * @param params
	 * @return
	 */
	protected List<T> query(String sql, int pageNo, int pageSize, Object[] params) {
		return new IndexCallback<>(sql, clz, pageNo, pageSize, params).doInHibernate(getSession());
	}


	/**
	 * 查询记录的条数
	 * @param sql
	 * @param params
	 * @return
	 */
	protected int queryCount(String sql, Map<String, Object> params) {
		Integer cnt = new MapCountCallback(sql, params).doInHibernate(getSession());
		return cnt == null ? 0 : cnt;
	}
	/**
	 * 查询记录的条数
	 * @param sql
	 * @return
	 */
	protected int queryCount(String sql) {
		return queryCount(sql, (Object[]) null);
	}
	/**
	 * 查询记录的条数
	 * @param sql
	 * @param params
	 * @return
	 */
	protected int queryCount(String sql, Object[] params) {
		Integer cnt = new IndexCountCallback(sql, params).doInHibernate(getSession());
		return cnt == null ? 0 : cnt;
	}


	/**
	 * 查询某个表的所有字段。只取单条记录
	 * @param sql
	 * @param params
	 * @return
	 */
	protected T queryForObject(String sql, Map<String, Object> params) {
		List<T> list = query(sql, params);
		return list == null || list.size() <= 0 ? null : list.get(0);
	}

	/**
	 * 查询某个表的所有字段。只取单条记录
	 * @param sql
	 * @param params
	 * @return
	 */
	protected T queryForObject(String sql, Object[] params) {
		List<T> list = query(sql, params);
		return list == null || list.size() <= 0 ? null : list.get(0);
	}
	/**
	 * 查询某个表的所有字段。只取单条记录
	 * @param sql
	 * @return
	 */
	protected T queryForObject(String sql) {
		return queryForObject(sql, (Object[]) null);
	}

	/**
	 * 执行增删改操作
	 * @param sql
	 * @param params
	 * @return
	 */
	protected int execute(String sql, Map<String, Object> params) {
		Integer cnt = new MapModifyCallback(sql, params).doInHibernate(getSession());
		return cnt == null ? 0 : cnt;
	}
	/**
	 * 执行增删改操作
	 * @param sql
	 * @param params
	 * @return
	 */
	protected int execute(String sql, Object[] params) {
		Integer cnt = new IndexModifyCallback(sql, params).doInHibernate(getSession());
		return cnt == null ? 0 : cnt;
	}
	/**
	 * 执行增删改操作
	 * @param sql
	 * @return
	 */
	protected int execute(String sql) {
		return execute(sql, (Object[]) null);
	}
}
