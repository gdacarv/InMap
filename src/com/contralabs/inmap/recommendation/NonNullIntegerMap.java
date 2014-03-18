package com.contralabs.inmap.recommendation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class NonNullIntegerMap<T> implements Map<T, Integer> {
	
	private Map<T, Integer> mMap;

	public NonNullIntegerMap(Map<T,Integer> map){
		if(map == null)
			throw new NullPointerException();
		mMap = map;
	}

	@Override
	public void clear() {
		mMap.clear();
	}

	@Override
	public boolean containsKey(Object arg0) {
		return mMap.containsKey(arg0);
	}

	@Override
	public boolean containsValue(Object value) {
		return mMap.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<T, Integer>> entrySet() {
		return mMap.entrySet();
	}

	@Override
	public Integer get(Object key) {
		Integer ret = mMap.get(key);
		if(ret == null){
			ret = Integer.valueOf(0);
		}
		return ret;
	}

	@Override
	public boolean isEmpty() {
		return mMap.isEmpty();
	}

	@Override
	public Set<T> keySet() {
		return mMap.keySet();
	}

	@Override
	public Integer put(T key, Integer value) {
		return mMap.put(key, value);
	}

	@Override
	public void putAll(Map<? extends T, ? extends Integer> arg0) {
		mMap.putAll(arg0);
	}

	@Override
	public Integer remove(Object key) {
		return mMap.remove(key);
	}

	@Override
	public int size() {
		return mMap.size();
	}

	@Override
	public Collection<Integer> values() {
		return mMap.values();
	}

}
