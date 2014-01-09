package com.contralabs.inmap.recommendation;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class NonNullDoubleMap<T> implements Map<T, Double> {
	
	private Map<T, Double> mMap;

	public NonNullDoubleMap(Map<T,Double> map){
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
	public Set<java.util.Map.Entry<T, Double>> entrySet() {
		return mMap.entrySet();
	}

	@Override
	public Double get(Object key) {
		Double ret = mMap.get(key);
		if(ret == null){
			ret = Double.valueOf(0);
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
	public Double put(T key, Double value) {
		return mMap.put(key, value);
	}

	@Override
	public void putAll(Map<? extends T, ? extends Double> arg0) {
		mMap.putAll(arg0);
	}

	@Override
	public Double remove(Object key) {
		return mMap.remove(key);
	}

	@Override
	public int size() {
		return mMap.size();
	}

	@Override
	public Collection<Double> values() {
		return mMap.values();
	}

}
