package br.edu.ufrj.lwcoedge.core.metrics.experiment;

import java.io.Serializable;

public class MetricCollected implements Serializable {

	private static final long serialVersionUID = 6597579755026097768L;

	private String metric;
	private MetricAmount metricAmount;
	private MetricAmountAndValue metricAmountAndValue;
	private MetricComputationTime metricComputationalTime;
	private MetricAmountAndTimes metricAmountAndTimes;

	public MetricCollected() {}

	public MetricCollected(String metric) {
		super();
		this.metric = metric;
	}

	/**
	 * @return the metric
	 */
	public String getMetric() {
		return metric;
	}

	/**
	 * @param metric the metric to set
	 */
	public void setMetric(String metric) {
		this.metric = metric;
	}


	/**
	 * @return the metricAmount
	 */
	public MetricAmount getMetricAmount() {
		return metricAmount;
	}


	/**
	 * @param metricAmount the metricAmount to set
	 */
	public void setMetricAmount(MetricAmount metricAmount) {
		this.metricAmount = metricAmount;
	}

	/**
	 * @return the metricComputationalTime
	 */
	public MetricComputationTime getMetricComputationalTime() {
		return metricComputationalTime;
	}

	/**
	 * @param metricComputationalTime the metricComputationalTime to set
	 */
	public void setMetricComputationalTime(MetricComputationTime metricComputationalTime) {
		this.metricComputationalTime = metricComputationalTime;
	}

	/**
	 * @return the metricAmountAndTimes
	 */
	public MetricAmountAndTimes getMetricAmountAndTimes() {
		return metricAmountAndTimes;
	}

	/**
	 * @param metricAmountAndTimes the metricAmountAndTimes to set
	 */
	public void setMetricAmountAndTimes(MetricAmountAndTimes metricAmountAndTimes) {
		this.metricAmountAndTimes = metricAmountAndTimes;
	}

	/**
	 * @return the metricAmountAndValue
	 */
	public MetricAmountAndValue getMetricAmountAndValue() {
		return metricAmountAndValue;
	}

	/**
	 * @param metricAmountAndValue the metricAmountAndValue to set
	 */
	public void setMetricAmountAndValue(MetricAmountAndValue metricAmountAndValue) {
		this.metricAmountAndValue = metricAmountAndValue;
	}

}
