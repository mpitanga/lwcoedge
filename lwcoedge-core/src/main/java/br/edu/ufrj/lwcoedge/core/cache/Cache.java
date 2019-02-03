package br.edu.ufrj.lwcoedge.core.cache;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.collections.map.LRUMap;

import br.edu.ufrj.lwcoedge.core.cache.mechanism.CrunchifyInMemoryCache;

public class Cache<K, T> implements Serializable {

	private static final long serialVersionUID = 804661052926508536L;
	
	// https://crunchify.com/how-to-create-a-simple-in-memory-cache-in-java-lightweight-cache/
    // Test with crunchifyTimeToLive = 0 seconds
    // crunchifyTimerInterval = 0 seconds
    // maxItems > 0

	private CrunchifyInMemoryCache<K, T> cache;
	
	public Cache() {}
	
	public Cache(long TimeToLive, final long TimerInterval, int maxItems) {
	    cache = new CrunchifyInMemoryCache<K, T>(TimeToLive, TimerInterval, maxItems);
	}

    public void put(K key, T value) {
    	cache.put(key, value);
    }

    public T get(K key) {
    	return cache.get(key);
    }

    public LRUMap get() {
    	return cache.get();
    }

	public Object getKeys() {
    	return cache.getKeys();
    }
    
    public void remove(K key) {
    	cache.remove(key);
    }

    public int size() {
    	return cache.size();
    }

    public ArrayList<T> getAll() {
    	return cache.getAll();
    }
    
    public void cleanup() {
    	cache.cleanup();
    }
    
    public void clearAll() {
    	cache.clearAll();
    }

}
