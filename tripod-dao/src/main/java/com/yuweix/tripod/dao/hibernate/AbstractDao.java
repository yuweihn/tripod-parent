package com.yuweix.tripod.dao.hibernate;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.yuweix.tripod.dao.PersistContext;
import com.yuweix.tripod.dao.sharding.Sharding;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionFactory;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.persistence.Id;


/**
 * @author yuwei
 */
public abstract class AbstractDao<T extends Serializable, PK extends Serializable> extends PersistContext implements Dao<T, PK> {
	private static final Map<Class<?>, FieldColumn> CLASS_PK_FIELD_MAP = new ConcurrentHashMap<>();
	private static final Map<Class<?>, FieldColumn> CLASS_SHARDING_FIELD_MAP = new ConcurrentHashMap<>();

	private final ThreadLocal<Interceptor> interceptorThreadLocal = new ThreadLocal<>();

	private Class<T> clz;
	private SessionFactory sessionFactory;


	@Resource
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected Session getSession() {
		Interceptor interceptor = interceptorThreadLocal.get();
		if (interceptor == null) {
			return this.sessionFactory.getCurrentSession();
		} else {
			SessionBuilder<?> builder = this.sessionFactory.withOptions().interceptor(interceptor);
			return builder.openSession();
		}
	}


	@SuppressWarnings("unchecked")
	public AbstractDao() {
		this.clz = null;
		Type t = getClass().getGenericSuperclass();
		if (t instanceof ParameterizedType) {
			this.clz = (Class<T>) ((ParameterizedType) t).getActualTypeArguments()[0];
		}
	}

	@Override
	public T get(final PK id) {
		return getSession().get(clz, id);
	}

	@Override
	public T get(PK id, Object shardingVal) {
		try {
			beforeSharding(shardingVal);
			return getSession().get(clz, id);
		} finally {
			afterSharding();
		}
	}

	private FieldColumn getPKFieldColumn() {
		FieldColumn fc = CLASS_PK_FIELD_MAP.get(clz);

		if (fc == null) {
			Field[] fields = clz.getDeclaredFields();
			for (Field field: fields) {
				field.setAccessible(true);
				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					Column col = field.getAnnotation(Column.class);
					fc = new FieldColumn(col == null ? field.getName() : col.name(), field);
					CLASS_PK_FIELD_MAP.put(clz, fc);
					break;
				}
			}
		}
		return fc;
	}
	private FieldColumn getShardingFieldColumn() {
		FieldColumn fc = CLASS_SHARDING_FIELD_MAP.get(clz);

		if (fc == null) {
			Field[] fields = clz.getDeclaredFields();
			for (Field field: fields) {
				field.setAccessible(true);
				Sharding sAnn = field.getAnnotation(Sharding.class);
				if (sAnn != null) {
					Column col = field.getAnnotation(Column.class);
					fc = new FieldColumn(col == null ? field.getName() : col.name(), field);
					CLASS_SHARDING_FIELD_MAP.put(clz, fc);
					break;
				}
			}
		}
		return fc;
	}
	private Object getShardingVal(T t) {
		Object shardingVal = null;
		FieldColumn fc = getShardingFieldColumn();
		if (fc != null) {
			try {
				shardingVal = fc.getField().get(t);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return shardingVal;
	}

	protected void beforeSharding(T t) {
		Object shardingVal = getShardingVal(t);
		beforeSharding(shardingVal);
	}
	protected void beforeSharding(Object shardingVal) {
		if (shardingVal == null) {
			return;
		}
		String srcTableName = this.getTableName(clz);
		String destTableName = this.getPhysicalTableName(clz, shardingVal);
		interceptorThreadLocal.set(new DynamicTableInterceptor(srcTableName, destTableName));
	}
	protected void afterSharding() {
		interceptorThreadLocal.remove();
	}

	@Override
	public void save(final T t) {
		try {
			beforeSharding(t);
			getSession().save(t);
		} finally {
			afterSharding();
		}
	}

	@Override
	public void update(final T t) {
		try {
			beforeSharding(t);
			getSession().update(t);
		} finally {
			afterSharding();
		}
	}

	@Override
	public void deleteByKey(PK id) {
		final T t = get(id);
		if (t != null) {
			getSession().delete(t);
		}
	}

	@Override
	public void deleteByKey(PK id, Object shardingVal) {
		final T t = get(id, shardingVal);
		if (t != null) {
			try {
				beforeSharding(t);
				getSession().delete(t);
			} finally {
				afterSharding();
			}
		}
	}





	/**
	 * 查询某个表的所有字段
	 * @param sql
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<T> query(String sql, Map<String, Object> params) {
		return (List<T>) new MapCallback(sql, clz, params).doInHibernate(getSession());
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
	@SuppressWarnings("unchecked")
	protected List<T> query(String sql, Object[] params) {
		return (List<T>) new IndexCallback(sql, clz, params).doInHibernate(getSession());
	}


	/**
	 * 查询某个表的所有字段。分页查询
	 * @param sql
	 * @param pageNo
	 * @param pageSize
	 * @param params
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<T> query(String sql, int pageNo, int pageSize, Map<String, Object> params) {
		return (List<T>) new MapCallback(sql, clz, pageNo, pageSize, params).doInHibernate(getSession());
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
	@SuppressWarnings("unchecked")
	protected List<T> query(String sql, int pageNo, int pageSize, Object[] params) {
		return (List<T>) new IndexCallback(sql, clz, pageNo, pageSize, params).doInHibernate(getSession());
	}


	/**
	 * 查询记录的条数
	 * @param sql
	 * @param params
	 * @return
	 */
	protected int queryCount(String sql, Map<String, Object> params) {
		return (Integer) new MapCntCallback(sql, params).doInHibernate(getSession());
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
		return (Integer) new IndexCntCallback(sql, params).doInHibernate(getSession());
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

	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> queryForMapList(String sql, int pageNo, int pageSize, Map<String, Object> params) {
		return (List<Map<String, Object>>) new MapCallback(sql, Map.class, pageNo, pageSize, params).doInHibernate(getSession());
	}
	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> queryForMapList(String sql, Map<String, Object> params) {
		return (List<Map<String, Object>>) new MapCallback(sql, Map.class, params).doInHibernate(getSession());
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

	protected List<Map<String, Object>> queryForMapList(String sql, int pageNo, int pageSize) {
		return queryForMapList(sql, pageNo, pageSize, (Object[]) null);
	}
	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> queryForMapList(String sql, int pageNo, int pageSize, Object[] params) {
		return (List<Map<String, Object>>) new IndexCallback(sql, Map.class, pageNo, pageSize, params).doInHibernate(getSession());
	}
	@SuppressWarnings("unchecked")
	protected List<Map<String, Object>> queryForMapList(String sql, Object[] params) {
		return (List<Map<String, Object>>) new IndexCallback(sql, Map.class, params).doInHibernate(getSession());
	}

	/**
	 * 执行增删改操作
	 * @param sql
	 * @param params
	 * @return
	 */
	protected int execute(String sql, Map<String, Object> params) {
		return (Integer) new MapModifyCallback(sql, params).doInHibernate(getSession());
	}
	/**
	 * 执行增删改操作
	 * @param sql
	 * @param params
	 * @return
	 */
	protected int execute(String sql, Object[] params) {
		return (Integer) new IndexModifyCallback(sql, params).doInHibernate(getSession());
	}
	/**
	 * 执行增删改操作
	 * @param sql
	 * @return
	 */
	protected int execute(String sql) {
		return execute(sql, (Object[]) null);
	}
	
	public void delete(final T t) {
		getSession().delete(t);
	}
}
