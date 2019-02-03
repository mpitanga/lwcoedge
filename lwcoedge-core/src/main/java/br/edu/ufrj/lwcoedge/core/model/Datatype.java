package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

/**
 * @author mpalv
 *
 */
public class Datatype implements Serializable {

	private static final long serialVersionUID = -1499390626935145496L;

	private String id;

	/**
	 * Default constructor
	 */
	public Datatype() {}

	/**
	 * @param id The data type identification. // id => where.who.what <=> UFRJ.UbicompLab.temperature
	 */
	public Datatype(String id) {
		super();
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id The data type identification to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}

}
