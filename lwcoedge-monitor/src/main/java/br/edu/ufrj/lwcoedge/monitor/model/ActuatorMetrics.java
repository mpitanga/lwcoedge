package br.edu.ufrj.lwcoedge.monitor.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ActuatorMetrics implements Serializable {

	private static final long serialVersionUID = -6233319848667461271L;

    private String name;
    private String description;
    private String baseUnit;
	private ArrayList<Measurements> measurements;
    private ArrayList<AvailableTags> availableTags;
    
	public ActuatorMetrics() {}

	/**
	 * @return the measurements
	 */
	public ArrayList<Measurements> getMeasurements() {
		return measurements;
	}

	/**
	 * @param measurements the measurements to set
	 */
	public void setMeasurements(ArrayList<Measurements> measurements) {
		this.measurements = measurements;
	}

	/**
	 * @return the baseUnit
	 */
	public String getBaseUnit() {
		return baseUnit;
	}

	/**
	 * @param baseUnit the baseUnit to set
	 */
	public void setBaseUnit(String baseUnit) {
		this.baseUnit = baseUnit;
	}

	/**
	 * @return the availableTags
	 */
	public ArrayList<AvailableTags> getAvailableTags() {
		return availableTags;
	}

	/**
	 * @param availableTags the availableTags to set
	 */
	public void setAvailableTags(ArrayList<AvailableTags> availableTags) {
		this.availableTags = availableTags;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Metrics [measurements=");
		builder.append(measurements);
		builder.append(", baseUnit=");
		builder.append(baseUnit);
		builder.append(", availableTags=");
		builder.append(availableTags);
		builder.append(", description=");
		builder.append(description);
		builder.append(", name=");
		builder.append(name);
		builder.append("]");
		return builder.toString();
	}

}
