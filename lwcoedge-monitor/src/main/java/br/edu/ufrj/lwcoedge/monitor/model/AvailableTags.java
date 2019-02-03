package br.edu.ufrj.lwcoedge.monitor.model;

import java.io.Serializable;
import java.util.ArrayList;

public class AvailableTags implements Serializable {

	private static final long serialVersionUID = 5238404493354398219L;

	private String tag;
	private ArrayList<String> values;
    
	public AvailableTags() {}

	/**
	 * @return the values
	 */
	public ArrayList<String> getValues() {
		return values;
	}

	/**
	 * @param values the values to set
	 */
	public void setValues(ArrayList<String> values) {
		this.values = values;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AvailableTags [values=");
		builder.append(values);
		builder.append(", tag=");
		builder.append(tag);
		builder.append("]");
		return builder.toString();
	}

}
