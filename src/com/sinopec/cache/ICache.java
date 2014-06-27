package com.sinopec.cache;

import com.sinopec.io.DynamicInfoSerializable;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * Created with IntelliJ IDEA. User: gaochuanjun Date: 13-10-24 Time: 上午11:06 To
 * change this template use File | Settings | File Templates.
 */
public interface ICache<K, V> {

	/**
	 * 获取缓存数据
	 * 
	 * @param key
	 * @return
	 */
	public V get(K key);

	/**
	 * 移出缓存数据
	 * 
	 * @param key
	 * @return
	 */
	public V remove(K key);

	/**
	 * 清除所有缓存内的数据
	 * 
	 * @return
	 */
	public boolean clear();

	/**
	 * 缓存数据数量
	 * 
	 * @return
	 */
	public int size();

	/**
	 * 缓存所有的key的集合
	 * 
	 * @return
	 */
	public Set<K> keySet();

	/**
	 * 缓存的所有value的集合
	 * 
	 * @return
	 */
	public Collection<DynamicInfoSerializable> values();

	/**
	 * 是否包含了指定key的数据
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(K key);

	/**
	 * 释放Cache占用的资源
	 */
	public void destroy();

	DynamicInfoSerializable put(String key, DynamicInfoSerializable value, int TTL);

	Object put(String key, DynamicInfoSerializable value, Date expiry);

	DynamicInfoSerializable put(String key, DynamicInfoSerializable value);
}
