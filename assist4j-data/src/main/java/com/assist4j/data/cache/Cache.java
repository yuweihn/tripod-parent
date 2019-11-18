package com.assist4j.data.cache;




/**
 * 缓存引擎接口
 * @author yuwei
 */
public interface Cache {
	/**
	 * 是否包含指定的key
	 * 找到返回为true，找不到返回为false
	 * @param key 查找的值
	 * @return 是否找到
	 */
	boolean contains(String key);

	/**
	 * 更新指定key的值
	 * @param key 缓存key。
	 * @param value 缓存的值。
	 * @param timeout 过期时间(s)。
	 * @return true更新成功，false更新失败。
	 */
	<T>boolean put(String key, T value, long timeout);

	/**
	 * 获取指定key的值
	 * @param key 缓存对象的key
	 * @return 查询到的缓存的对象
	 */
	<T>T get(String key);

	/**
	 * 删除指定key
	 * @param key 缓存对象的key
	 */
	void remove(String key);
}
