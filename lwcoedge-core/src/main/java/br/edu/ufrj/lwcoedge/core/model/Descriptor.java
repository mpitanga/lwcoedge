/**
 * 
 */
package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.edu.ufrj.lwcoedge.core.util.Util;

/**
 * @author mpalv
 *
 */
public class Descriptor implements Serializable {
	private static final long serialVersionUID = 8911426142264635229L;
	
	private DescriptorID id;
	private String description;
	private Type type;
	private ArrayList<Element> element = new ArrayList<Element>();
	
	public Descriptor() {}

	/**
	 * @param id
	 * @param description
	 * @param type
	 */
	public Descriptor(DescriptorID id, String description, Type type) {
		super();
		this.id = id;
		this.description = description;
		this.type = type;
	}

	/**
	 * @param id
	 * @param description
	 * @param type
	 * @param element
	 */
	public Descriptor(DescriptorID id, String description, Type type, ArrayList<Element> element) {
		super();
		this.id = id;
		this.description = description;
		this.type = type;
		this.element = element;
	}

	@JsonIgnore
	public String getDescriptorId() {
		return this.id.getWhere().concat(".")
				.concat(this.id.getWho()).concat(".")
				.concat(this.id.getWhat());
	}

	/**
	 * @return the id
	 */
	public DescriptorID getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(DescriptorID id) {
		this.id = id;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}
	/**
	 * @return the element
	 */
	public ArrayList<Element> getElement() {
		return element;
	}
	/**
	 * @param element the element to set
	 */
	public void setElement(ArrayList<Element> element) {
		this.element = element;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	
	@Override
	public String toString() {
		return Util.obj2json(this);
	}
	
/*	
	public static void main(String[] args) {
		 for (Type type : Type.values()) {
			 System.out.println(type);
		 }
	}
*/	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Descriptor other = (Descriptor) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
