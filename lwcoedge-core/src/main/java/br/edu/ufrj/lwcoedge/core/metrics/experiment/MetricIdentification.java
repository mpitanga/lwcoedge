package br.edu.ufrj.lwcoedge.core.metrics.experiment;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class MetricIdentification {

	private String experiment;
	private String metric;
	private String variation;
	private String datatypeID;
	
	public MetricIdentification(String experiment, String metric, String variation, String datatypeID) {
		super();
		this.experiment = experiment;
		this.metric = metric;
		this.variation = variation;
		this.datatypeID = datatypeID;
	}

	//Analytic key -> E1.200-M3-0-UFRJ.UbicompLab.temperature
	//Summary key  -> E1.200-M3-UFRJ.UbicompLab.temperature
	public MetricIdentification(String identification) {
		String[] idSplit = identification.split("-");
		this.experiment = idSplit[0];
		this.metric = idSplit[1];
		if (idSplit.length==4) {
			this.variation = idSplit[2];
			this.datatypeID = idSplit[3];			
		} else {
			this.variation = null;
			this.datatypeID = idSplit[2];			
		}
	}

	/**
	 * @return experiment key
	 */
	public String getKey() {
		return Util.msg(this.experiment, "-", this.metric);
	}

	/**
	 * @return experiment extended key
	 */
	public String getSummaryKey() {
		return Util.msg(this.getKey(), "-", this.datatypeID);
	}

	/**
	 * @return the experiment
	 */
	public String getExperiment() {
		return experiment;
	}

	/**
	 * @param experiment the experiment to set
	 */
	public void setExperiment(String experiment) {
		this.experiment = experiment;
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
	 * @return the variation
	 */
	public String getVariation() {
		return variation;
	}

	/**
	 * @param variation the variation to set
	 */
	public void setVariation(String variation) {
		this.variation = variation;
	}

	/**
	 * @return the datatypeID
	 */
	public String getDatatypeID() {
		return datatypeID;
	}

	/**
	 * @param datatypeID the datatypeID to set
	 */
	public void setDatatypeID(String datatypeID) {
		this.datatypeID = datatypeID;
	}

	@Override
	public String toString() {
		if (this.variation == null)
			return Util.msg(this.experiment, "-", this.metric, "-", "0", "-", this.datatypeID);
		else
			return Util.msg(this.experiment, "-", this.metric, "-", this.variation, "-", this.datatypeID);
	}
}
