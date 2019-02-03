package br.com.context.broker.elements;

import java.io.Serializable;
import java.util.List;

public class ContextElement implements Serializable {

	private static final long serialVersionUID = -6559613728331031299L;

	private String id;
    private List<Attributes> attributes;
    private String type;
    private String isPattern;
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the attributes
	 */
	public List<Attributes> getAttributes() {
		return attributes;
	}
	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(List<Attributes> attributes) {
		this.attributes = attributes;
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
	 * @return the isPattern
	 */
	public String getIsPattern() {
		return isPattern;
	}
	/**
	 * @param isPattern the isPattern to set
	 */
	public void setIsPattern(String isPattern) {
		this.isPattern = isPattern;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ContextElement [id=" + id + ", attributes=" + attributes + ", type=" + type + ", isPattern=" + isPattern
				+ "]";
	}
    
}
