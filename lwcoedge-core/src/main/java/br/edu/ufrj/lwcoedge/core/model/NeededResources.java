package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class NeededResources implements Serializable {

	private static final long serialVersionUID = 6921207617209775476L;
	
	private String type;
	private Resources resources;
	
	public NeededResources() {}
	
	/**
	 * @param type
	 * @param resources
	 */
	public NeededResources(String type, Resources resources) {
		super();
		this.type = type;
		this.resources = resources;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the resources
	 */
	public Resources getResources() {
		return this.resources;
	}

	/**
	 * @param resources the resources to set
	 */
	public void setResources(Resources resources) {
		this.resources = resources;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}
	
}
