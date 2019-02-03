package br.edu.ufrj.lwcoedge.experiment.core.model;

import java.io.Serializable;

public class MetricCTAmount extends MetricCT implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2412775951746071548L;

	private Long amountOf;
	
	public MetricCTAmount() {}

	/**
	 * @return the amountOf
	 */
	public Long getAmountOf() {
		return amountOf;
	}

	/**
	 * @param amountOf the amountOf to set
	 */
	public void setAmountOf(Long amountOf) {
		this.amountOf = amountOf;
	}

}
