package br.edu.ufrj.lwcoedge.core.metrics.experiment;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class MetricAmountAndValue extends AbstractMetric implements Serializable {

	private static final long serialVersionUID = -6608862326854169229L;
	
	private Integer amountOf;
	private Long valueOf;
	
	public MetricAmountAndValue() {}
	
	/**
	 * 
	 * @param id
	 * @param valueOf
	 * @param amountOf
	 */
	public MetricAmountAndValue(String id, Long valueOf, Integer amountOf) {
		super(id);
		this.amountOf = amountOf;
		this.valueOf = valueOf;
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
	 * @return the valueOf
	 */
	public Long getValueOf() {
		return valueOf;
	}

	/**
	 * @param valueOf the valueOf to set
	 */
	public void setValueOf(Long valueOf) {
		this.valueOf = valueOf;
	}
	
	@Override
	public String toString() {
		return Util.obj2json(this);
	}

}
