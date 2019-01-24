package com.android.utils;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 将传递进来的V转换成软引用的对象
 * @author wangyang
 * @2014-2-25 上午10:42:42
 * @param <K>
 * @param <V>
 */
@SuppressWarnings("serial")
public class SoftMap<K, V> extends HashMap<K, V>{
	// 将传递进来的V转换成软引用的对象,放到当前的集合中
	private Map<K,HIptvSoft<K, V>> map;
	private ReferenceQueue<V> q;
	
	public SoftMap() {
		map = new HashMap<K, HIptvSoft<K,V>>();
		q = new ReferenceQueue<V>();
	}
	
	@Override
	public boolean containsKey(Object key) {
		V v =  this.get(key);
		if(v!= null)
			return true;
		return false;
	}

	@Override
	public V get(Object key) {
		//清理当前的集合
		clealSoftReference();
		HIptvSoft<K, V> sf = map.get(key);
		if(sf!=null)
			return sf.get();
		return null;
	}

	@Override
	public V put(K key, V value) {
		 map.put(key, new HIptvSoft<K, V>(key, value, q));
		 return null;
	}
   
	@SuppressWarnings("unchecked")
	public void clealSoftReference(){
		Reference<? extends V> poll =  q.poll();
		while(poll!=null){
			HIptvSoft<K, V> sr = (HIptvSoft<K, V>) poll;
			map.remove(sr.key);
			poll = q.poll();
		}
	}
	
	@SuppressWarnings("hiding")
	private class HIptvSoft<K,V> extends SoftReference<V>{
		private K key;

		public HIptvSoft(K k,V v, ReferenceQueue<? super V> q) {
			super(v, q);
			this.key = k;
		}
		
	}
}
