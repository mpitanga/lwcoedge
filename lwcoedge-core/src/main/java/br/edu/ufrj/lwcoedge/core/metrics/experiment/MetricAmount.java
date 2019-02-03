package br.edu.ufrj.lwcoedge.core.metrics.experiment;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class MetricAmount extends AbstractMetric implements Serializable {

	private static final long serialVersionUID = -294716878544217781L;

	private Integer amountOf;
	
	public MetricAmount() {}
	
	public MetricAmount(String id) {
		super(id);
		this.amountOf = 0;
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

	@Override
	public String toString() {
		return Util.obj2json(this);
	}

}
