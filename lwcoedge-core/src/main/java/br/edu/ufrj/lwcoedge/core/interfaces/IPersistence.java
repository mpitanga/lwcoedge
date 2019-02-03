package br.edu.ufrj.lwcoedge.core.interfaces;

import br.edu.ufrj.lwcoedge.core.model.VirtualNode;

public interface IPersistence {
	/**
	 * Registering a new virtual node into the cache
	 * @param value A virtual node instance
	 */
    public void register(VirtualNode value, String... args);
    
    /**
     * Remove a virtual node instance from the cache
     * @param key Datatype id
     */
    public void remove(String key, String... args);

    /**
     * Clear the current cache
     */
	public void empty(String... args);
}
