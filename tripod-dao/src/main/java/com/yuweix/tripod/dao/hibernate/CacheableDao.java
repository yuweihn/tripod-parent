package com.yuweix.tripod.dao.hibernate;


import com.yuweix.tripod.dao.PersistCache;
import com.yuweix.tripod.dao.PersistUtil;
import com.yuweix.tripod.sharding.annotation.Shard;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.lang.reflect.Field;


/**
 * @author yuwei
 */
public abstract class CacheableDao<T extends Serializable, PK extends Serializable> extends AbstractDao<T, PK> {
	public static final long DEFAULT_CACHE_TIMEOUT = 3600;

	@Autowired
	protected PersistCache cache;


	public CacheableDao() {

	}


	@Override
	public T get(PK id) {
		String key = getPkCacheKeyPre() + id;
		T t = cache.get(key);
		if (t != null) {
			return t;
		}

		t = getSession().get(clz, id);
		if (t != null) {
			cache.put(key, t, DEFAULT_CACHE_TIMEOUT);
			return t;
		} else {
			return null;
		}
	}

	public void deleteByIdFromCache(PK id) {
		String key = getPkCacheKeyPre() + id;
		cache.remove(key);
	}

	@Override
	public T get(@Shard Object shardingVal, PK id) {
		return this.get(id);
	}

	protected String getPkCacheKeyPre() {
		String appName = getAppName();
		String str = appName == null || "".equals(appName.trim()) ? "" : "." + appName.trim();
		return "cache" + str + "." + clz.getName() + ".by.pk.";
	}
	protected String getAppName() {
		return null;
	}

	private void handleChange(T t) {
		PK id = getId(t);
		deleteByIdFromCache(id);
		onchange(t);
	}
	@SuppressWarnings("unchecked")
	private PK getId(T t) {
		PK id = null;
		Field field = PersistUtil.getPKField(clz);
		if (field != null) {
			try {
				id = (PK) field.get(t);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		if (id == null) {
			throw new RuntimeException("数据异常");
		}
		return id;
	}

	/**
	 * 当参数指定的数据发生变化之后触发的行为
	 * @param t
	 */
	protected abstract void onchange(T t);

	@Override
	public void save(@Shard T t) {
		getSession().persist(t);
		if (t != null) {
			handleChange(t);
		}
	}

	@Override
	public void update(@Shard T t) {
		PK id = getId(t);
		T t0 = getSession().get(clz, id);

		getSession().merge(t);
		if (t != null) {
			handleChange(t);
		}
		if (t0 != null) {
			handleChange(t0);
		}
	}

	@Override
	public void delete(@Shard final T t) {
		getSession().remove(t);
		if (t != null) {
			handleChange(t);
		}
	}

	@Override
	public void deleteByKey(PK id) {
		T t = get(id);
		if (t == null) {
			return;
		}
		delete(t);
	}

	@Override
	public void deleteByKey(@Shard Object shardingVal, PK id) {
		T t = get(shardingVal, id);
		if (t == null) {
			return;
		}
		delete(t);
	}
}
