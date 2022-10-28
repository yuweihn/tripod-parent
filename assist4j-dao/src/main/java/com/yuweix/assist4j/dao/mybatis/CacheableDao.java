package com.yuweix.assist4j.dao.mybatis;


import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author yuwei
 */
public abstract class CacheableDao<T extends Serializable, PK extends Serializable> extends AbstractDao<T, PK> {
	private static final Map<Class<?>, Field> CLASS_PK_FIELD_ID_MAP = new ConcurrentHashMap<>();


	public CacheableDao() {

	}


	@Override
	public T get(PK id) {
		String key = getPkCacheKeyPre() + id;
		T t = getByCacheKey(key);
		if (t != null) {
			return t;
		}

		t = getMapper().selectOneById(id, clz);
		if (t != null) {
			putByCacheKey(key, t);
			return t;
		} else {
			return null;
		}
	}

	public void deleteByIdFromCache(PK id) {
		String key = getPkCacheKeyPre() + id;
		deleteByCacheKey(key);
	}

	protected String getPkCacheKeyPre() {
		return "cache." + clz.getName() + ".by.pk.";
	}
	protected abstract T getByCacheKey(String key);
	/**
	 * @param key
	 * @param t
	 * @return
	 */
	protected abstract void putByCacheKey(String key, T t);
	protected abstract void deleteByCacheKey(String key);

	private Field getPKField() {
		Field f = CLASS_PK_FIELD_ID_MAP.get(clz);

		if (f == null) {
			Field[] fields = clz.getDeclaredFields();
			for (Field field: fields) {
				field.setAccessible(true);
				Id idAnn = field.getAnnotation(Id.class);
				if (idAnn != null) {
					f = field;
					CLASS_PK_FIELD_ID_MAP.put(clz, field);
					break;
				}
			}
		}
		return f;
	}

	private void handleChange(T t) {
		deleteByIdFromCache(getId(t));
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

	/**
	 * 当参数指定的数据发生变化之后触发的行为
	 * @param t
	 */
	protected abstract void onchange(T t);

	@Override
	public int insert(T t) {
		int n = getMapper().insert(t);
		if (t != null) {
			handleChange(t);
		}
		return n;
	}

	@Override
	public int insertSelective(T t) {
		int n = getMapper().insertSelective(t);
		if (t != null) {
			handleChange(t);
		}
		return n;
	}

	@Override
	public int updateByPrimaryKey(T t) {
		PK id = getId(t);
		T t0 = getMapper().selectOneById(id, clz);

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
	public int updateByPrimaryKeySelective(T t) {
		PK id = getId(t);
		T t0 = getMapper().selectOneById(id, clz);

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
	public int delete(T t) {
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
}
