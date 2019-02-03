package br.edu.ufrj.lwcoedge.experiment.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class MetricCTAndAmount implements Serializable {

	private static final long serialVersionUID = -3534287898297878004L;

	private String id;
	private Long computationinMillis;
	private Long computationinSeconds;
	private Long amountOf;

	public MetricCTAndAmount() {}

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
	 * @return the computationinMillis
	 */
	public Long getComputationinMillis() {
		return computationinMillis;
	}

	/**
	 * @param computationinMillis the computationinMillis to set
	 */
	public void setComputationinMillis(Long computationinMillis) {
		this.computationinMillis = computationinMillis;
	}

	/**
	 * @return the computationinSeconds
	 */
	public Long getComputationinSeconds() {
		return computationinSeconds;
	}

	/**
	 * @param computationinSeconds the computationinSeconds to set
	 */
	public void setComputationinSeconds(Long computationinSeconds) {
		this.computationinSeconds = computationinSeconds;
	}

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

	@Override
	public String toString() {
		return Util.obj2json(this);
	}
}
