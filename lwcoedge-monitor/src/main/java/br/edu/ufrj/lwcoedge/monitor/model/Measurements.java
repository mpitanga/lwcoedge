/**
 * 
 */
package br.edu.ufrj.lwcoedge.monitor.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author mpalv
 *
 */
public class Measurements implements Serializable {

	private static final long serialVersionUID = 6286429443721283287L;
	
	private String statistic;
    private BigDecimal value;

	public Measurements() {}
	
	/**
	 * @return the statistic
	 */
	public String getStatistic() {
		return statistic;
	}

	/**
	 * @param statistic the statistic to set
	 */
	public void setStatistic(String statistic) {
		this.statistic = statistic;
	}

	/**
	 * @return the value
	 */
	public BigDecimal getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(BigDecimal value) {
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Measurements [statistic=");
		builder.append(statistic);
		builder.append(", value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}

}
