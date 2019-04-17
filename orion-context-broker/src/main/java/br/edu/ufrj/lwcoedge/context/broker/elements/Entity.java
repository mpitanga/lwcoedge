package br.edu.ufrj.lwcoedge.context.broker.elements;

public class Entity {
	private String id;
	private String type;
	private String isPattern;
	
	public Entity() {}
	
	/**
	 * @param id
	 * @param type
	 * @param isPattern
	 */
	public Entity(String id, String type, String isPattern) {
		super();
		this.id = id;
		this.type = type;
		this.isPattern = isPattern;
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
	
}
