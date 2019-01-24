package com.android.sqlite.table;

import android.support.v4.util.LruCache;
/**
 * 数据库数据缓存,以映射的Class与数据主键缓存至集合
 *
 * @author wangyang
 * 2014-7-16 上午11:38:35
 */
@SuppressWarnings("unchecked")
public class EntityTempCache {

	private EntityTempCache() {
	}
	
	/**
	 * 数据缓存集合
	 */
	private static LruCache<Class<?>, LruCache<String, Object>> cache = new LruCache<Class<?>, LruCache<String, Object>>(1024 * 1024 * 5);
	
	/**
	 * 清空缓存集合,
	 *
	 * @author wangyang
	 * 2014-7-16 上午11:40:37
	 */
	public static void clear() {
		cache = new LruCache<Class<?>, LruCache<String, Object>>(1024 * 1024 * 5);
	}
	
	/**
	 * 将数据主键值,与数据保存至缓存
	 *
	 * @author wangyang
	 * 2014-7-16 上午11:41:07
	 * @param id
	 * @param value
	 */
	public static synchronized void put(String id, Object value) {
		LruCache<String, Object> subCache = cache.get(value.getClass());
		if (subCache == null)
			subCache = new LruCache<String, Object>(1024 * 1024);
		subCache.put(id, value);
		cache.put(value.getClass(), subCache);
	}
	
	/**
	 * 获取数据
	 *
	 * @author wangyang
	 * 2014-7-16 上午11:41:36
	 * @param clazz
	 * @param id
	 * @return
	 */
	public static <T> T get(Class<T> clazz, String id) {
		LruCache<String, Object> subCache = cache.get(clazz);
		if (subCache == null)
			return null;
		return (T) subCache.get(id);
	}
	
	/**
	 * 根据主键删除数据
	 *
	 * @author wangyang
	 * 2014-7-16 上午11:41:44
	 * @param clazz
	 * @param id
	 * @return
	 */
	public static synchronized <T> T remove(Class<T> clazz, String id) {
		LruCache<String, Object> subCache = cache.get(clazz);
		if (subCache == null)
			return null;
		return (T) subCache.remove(id);
	}
	
	/**
	 * 删除某张表缓存数据
	 *
	 * @author wangyang
	 * 2014-7-16 上午11:42:03
	 * @param clazz
	 * @return
	 */
	public static synchronized <T> T remove(Class<T> clazz) {
		return (T) cache.remove(clazz);
	}
	
	/**
	 * 判断是否存在缓存数据
	 *
	 * @author wangyang
	 * 2014-7-16 上午11:42:16
	 * @param clazz
	 * @param id
	 * @return
	 */
	public static boolean containsKey(Class<?> clazz, String id) {
		return get(clazz, id) != null;
	}
}
