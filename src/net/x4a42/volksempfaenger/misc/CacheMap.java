package net.x4a42.volksempfaenger.misc;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class CacheMap<K, V> {

	private Map<K, SoftReference<V>> map;

	public CacheMap(int initialCapacity, float loadFactor) {
		map = new HashMap<K, SoftReference<V>>(initialCapacity, loadFactor);
	}

	public CacheMap(int initialCapacity) {
		this(initialCapacity, 0.75f);
	}

	public CacheMap() {
		this(16);
	}

	public void clear() {
		map.clear();
	}

	public V get(Object key) {
		SoftReference<V> valueRef = map.get(key);
		if (valueRef != null) {
			V value = valueRef.get();
			if (value == null) {
				map.remove(key);
			}
			return value;
		} else {
			return null;
		}
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public void put(K key, V value) {
		map.put(key, new SoftReference<V>(value));
	}

	public V remove(Object key) {
		SoftReference<V> valueRef = map.remove(key);
		if (valueRef != null) {
			return valueRef.get();
		} else {
			return null;
		}
	}

	public int size() {
		return map.size();
	}

}
