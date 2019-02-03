package br.edu.ufrj.lwcoedge.experiment.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class ExperimentSubmitConfig implements Serializable {

	private static final long serialVersionUID = -537505966520302590L;

    private String experimentname;
	private String basepath;
    private String[] edgenodes;
    private boolean executeexperiment;
    private boolean generateresults;
    private boolean clearmetrics;
       
	public ExperimentSubmitConfig() {}

	/**
	 * @return the experimentname
	 */
	public String getExperimentname() {
		return experimentname;
	}

	/**
	 * @param experimentname the experimentname to set
	 */
	public void setExperimentname(String experimentname) {
		this.experimentname = experimentname;
	}

	/**
	 * @return the basepath
	 */
	public String getBasepath() {
		return basepath;
	}

	/**
	 * @param basepath the basepath to set
	 */
	public void setBasepath(String basepath) {
		this.basepath = basepath;
	}

	/**
	 * @return the edgenodes
	 */
	public String[] getEdgenodes() {
		return edgenodes;
	}

	/**
	 * @param edgenodes the edgenodes to set
	 */
	public void setEdgenodes(String[] edgenodes) {
		this.edgenodes = edgenodes;
	}

	/**
	 * @return the executeexperiment
	 */
	public boolean isExecuteexperiment() {
		return executeexperiment;
	}

	/**
	 * @param executeexperiment the executeexperiment to set
	 */
	public void setExecuteexperiment(boolean executeexperiment) {
		this.executeexperiment = executeexperiment;
	}

	/**
	 * @return the generateresults
	 */
	public boolean isGenerateresults() {
		return generateresults;
	}

	/**
	 * @param generateresults the generateresults to set
	 */
	public void setGenerateresults(boolean generateresults) {
		this.generateresults = generateresults;
	}

	/**
	 * @return the clearmetrics
	 */
	public boolean isClearmetrics() {
		return clearmetrics;
	}

	/**
	 * @param clearmetrics the clearmetrics to set
	 */
	public void setClearmetrics(boolean clearmetrics) {
		this.clearmetrics = clearmetrics;
	}

	@Override
	public String toString() {
		return Util.obj2json(this);
	}
}
