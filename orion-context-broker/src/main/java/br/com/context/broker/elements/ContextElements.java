package br.com.context.broker.elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContextElements implements Serializable {

	private static final long serialVersionUID = -4009552484704070696L;

	private String id;
    private List<Attributes> attributes = new ArrayList<Attributes>();
    private String type;
    private String isPattern;

    public ContextElements() {
    	this.isPattern = "false";
    }
    
	/**
	 * @param id
	 * @param attributes
	 * @param type
	 * @param isPattern
	 */
	public ContextElements(String id, String type, List<Attributes> attributes) {
		super();
		this.id = id;
		this.attributes = attributes;
		this.type = type;
	}

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

    @Override
    public String toString() {
        return "ContextElements [id = "+id+", attributes = "+attributes+", type = "+type+", isPattern = "+isPattern+"]";
    }

}
