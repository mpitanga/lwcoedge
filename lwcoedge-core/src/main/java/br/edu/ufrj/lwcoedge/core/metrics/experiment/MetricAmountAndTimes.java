package br.edu.ufrj.lwcoedge.core.metrics.experiment;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class MetricAmountAndTimes extends AbstractMetric implements Serializable {

	private static final long serialVersionUID = -294716878544217781L;

	private Integer amountOf;
	private Long computationinMillis;
	private Long computationinSeconds;
	
	public MetricAmountAndTimes() {}
	
	public MetricAmountAndTimes(String id, LocalDateTime start, LocalDateTime finish, Integer amountOf) {
		super(id);
		this.amountOf = amountOf;

		Duration d = Duration.between(start, finish);
		this.computationinMillis = d.toMillis();
		this.computationinSeconds= d.getSeconds();

	}

	/**
	 * @return the amountOf
	 */
	public Integer getAmountOf() {
		return amountOf;
	}

	/**
	 * @param amountOf the amountOf to set
	 */
	public void setAmountOf(Integer amountOf) {
		this.amountOf = amountOf;
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

	@Override
	public String toString() {
		return Util.obj2json(this);
	}
}
