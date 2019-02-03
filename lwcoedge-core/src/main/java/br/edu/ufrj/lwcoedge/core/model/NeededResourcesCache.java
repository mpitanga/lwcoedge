package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;
import java.lang.annotation.Native;

import br.edu.ufrj.lwcoedge.core.cache.Cache;
import br.edu.ufrj.lwcoedge.core.util.Util;

public class NeededResourcesCache implements Serializable{

	private static final long serialVersionUID = -9062934531713418177L;

	// This constant defines the amount of data types per edge node
	@Native private static int MAX_ELEMENTS = 10;
	@Native private static int TIMETOLIVE = 0; // no expires
	@Native private static int TIMEINTERVAL = 0; // no expires

	private Cache<String, Resources> cache = new Cache<String, Resources>(TIMETOLIVE, TIMEINTERVAL, MAX_ELEMENTS);
	
	public NeededResourcesCache() {}
	
    public void put(String key, Resources value) {
    	cache.put(key, value);
    }

    public Resources get(String key) {
    	return cache.get(key);
    }

	public Object getAll() {
		return cache.getAll();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}

}
