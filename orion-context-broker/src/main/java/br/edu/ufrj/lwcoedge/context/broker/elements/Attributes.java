package br.edu.ufrj.lwcoedge.context.broker.elements;

import java.io.Serializable;

public class Attributes implements Serializable {

	private static final long serialVersionUID = -5092446213509756638L;

	private String name;
    private String value;
    private String type;
    
    public Attributes() {}
    
	/**
	 * @param name
	 * @param value
	 * @param type
	 */
	public Attributes(String name, String value, String type) {
		super();
		this.name = name;
		this.value = value;
		this.type = type;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
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
    
	@Override
    public String toString() {
        return "Attributes [name = "+name+", value = "+value+", type = "+type+"]";
    }
}
