package com.yuweix.tripod.dao.mybatis;


import com.yuweix.tripod.dao.PersistCache;
import com.yuweix.tripod.dao.sharding.Shard;
import com.yuweix.tripod.dao.sharding.Sharding;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author yuwei
 */
public abstract class CacheableDao<T extends Serializable, PK extends Serializable> extends AbstractDao<T, PK> {
	public static final long DEFAULT_CACHE_TIMEOUT = 3600;
	private static final Map<Class<?>, Field> CLASS_PK_FIELD_MAP = new ConcurrentHashMap<>();
	private static final Map<Class<?>, Field> CLASS_SHARDING_FIELD_MAP = new ConcurrentHashMap<>();

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

		t = getMapper().selectOneById(id, clz);
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
	public T get(PK id, @Shard Object shardingVal) {
		String key = getPkCacheKeyPre() + id + ".sharding." + shardingVal;
		T t = cache.get(key);
		if (t != null) {
			return t;
		}

		t = getMapper().selectOneByIdSharding(id, shardingVal, clz);
		if (t != null) {
			cache.put(key, t, DEFAULT_CACHE_TIMEOUT);
			return t;
		} else {
			return null;
		}
	}

	public void deleteByIdFromCache(PK id, Object shardingVal) {
		String key = getPkCacheKeyPre() + id + ".sharding." + shardingVal;
		cache.remove(key);
	}

	protected String getPkCacheKeyPre() {
		String appName = getAppName();
		String str = appName == null || "".equals(appName.trim()) ? "" : "." + appName.trim();
		return "cache" + str + "." + clz.getName() + ".by.pk.";
	}
	protected String getAppName() {
		return null;
	}

	private Field getPKField() {
		Field f = CLASS_PK_FIELD_MAP.get(clz);

		if (f == null) {
			Field[] fields = clz.getDeclaredFields();
			for (Field field: fields) {
				field.setAccessible(true);
				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					f = field;
					CLASS_PK_FIELD_MAP.put(clz, field);
					break;
				}
			}
		}
		return f;
	}
	private Field getShardingField() {
		Field f = CLASS_SHARDING_FIELD_MAP.get(clz);

		if (f == null) {
			Field[] fields = clz.getDeclaredFields();
			for (Field field: fields) {
				field.setAccessible(true);
				Sharding sAnn = field.getAnnotation(Sharding.class);
				if (sAnn != null) {
					f = field;
					CLASS_SHARDING_FIELD_MAP.put(clz, field);
					break;
				}
			}
		}
		return f;
	}

	private void handleChange(T t) {
		PK id = getId(t);
		Object shardingVal = getShardingVal(t);
		if (shardingVal == null) {
			deleteByIdFromCache(id);
		} else {
			deleteByIdFromCache(id, shardingVal);
		}
		onchange(t);
	}
	@SuppressWarnings("unchecked")
	private PK getId(T t) {
		PK id = null;
		Field field = getPKField();
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
	private Object getShardingVal(T t) {
		Object shardingVal = null;
		Field field = getShardingField();
		if (field != null) {
			try {
				shardingVal = field.get(t);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return shardingVal;
	}

	/**
	 * 当参数指定的数据发生变化之后触发的行为
	 * @param t
	 */
	protected abstract void onchange(T t);

	@Override
	public int insert(@Shard T t) {
		int n = getMapper().insert(t);
		if (t != null) {
			handleChange(t);
		}
		return n;
	}

	@Override
	public int insertSelective(@Shard T t) {
		int n = getMapper().insertSelective(t);
		if (t != null) {
			handleChange(t);
		}
		return n;
	}

	@Override
	public int updateByPrimaryKey(@Shard T t) {
		PK id = getId(t);
		Object shardingVal = getShardingVal(t);
		T t0 = null;
		if (shardingVal == null) {
			t0 = getMapper().selectOneById(id, clz);
		} else {
			t0 = getMapper().selectOneByIdSharding(id, shardingVal, clz);
		}

		int n = getMapper().updateByPrimaryKey(t);
		if (t != null) {
			handleChange(t);
		}
		if (t0 != null) {
			handleChange(t0);
		}
		return n;
	}

	@Override
	public int updateByPrimaryKeySelective(@Shard T t) {
		PK id = getId(t);
		Object shardingVal = getShardingVal(t);
		T t0 = null;
		if (shardingVal == null) {
			t0 = getMapper().selectOneById(id, clz);
		} else {
			t0 = getMapper().selectOneByIdSharding(id, shardingVal, clz);
		}

		int n = getMapper().updateByPrimaryKeySelective(t);
		if (t != null) {
			handleChange(t);
		}
		if (t0 != null) {
			handleChange(t0);
		}
		return n;
	}

	@Override
	public int delete(@Shard T t) {
		int n = getMapper().delete(t);
		if (t != null) {
			handleChange(t);
		}
		return n;
	}

	@Override
	public int deleteByKey(PK id) {
		T t = get(id);
		if (t == null) {
			return 0;
		}
		return delete(t);
	}

	@Override
	public int deleteByKey(PK id, @Shard Object shardingVal) {
		T t = get(id, shardingVal);
		if (t == null) {
			return 0;
		}
		return delete(t);
	}
}
