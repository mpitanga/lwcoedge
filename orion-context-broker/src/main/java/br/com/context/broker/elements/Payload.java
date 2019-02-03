package br.com.context.broker.elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Payload implements Serializable {
	private static final long serialVersionUID = 3471891139244300534L;
	private List<Entity> entities = new ArrayList<Entity>();
	
	public Payload() {}

	/**
	 * @return the entities
	 */
	public List<Entity> getEntities() {
		return entities;
	}

	/**
	 * @param entities the entities to set
	 */
	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
	
}
