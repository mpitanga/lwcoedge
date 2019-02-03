package br.edu.ufrj.lwcoedge.experiment.core.model;

import java.io.Serializable;

public class ExperimentConfig implements Serializable {

	private static final long serialVersionUID = 4045689104402805886L;

	private String basepath;
    private String[] edgenodes;
    private String experimentname;
    private String callbackurl;
    private Integer times;
    private Integer entrypointport;
    private Integer idxnode;
    private Integer cycles;
    private Integer requestvariation;
    private Integer maxfreshness;
    private Integer maxresponsetime;
    private boolean executeexperiment;
    private boolean generateresults;
    private boolean clearmetrics;
    private boolean randomfreshness;
    private String[] datatypeids;
    private Integer idxdatatype;

    private Collaboration collaboration;
       
	public ExperimentConfig() {}

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
	 * @return the callbackurl
	 */
	public String getCallbackurl() {
		return callbackurl;
	}

	/**
	 * @param callbackurl the callbackurl to set
	 */
	public void setCallbackurl(String callbackurl) {
		this.callbackurl = callbackurl;
	}

	/**
	 * @return the times
	 */
	public Integer getTimes() {
		return times;
	}

	/**
	 * @param times the times to set
	 */
	public void setTimes(Integer times) {
		this.times = times;
	}

	/**
	 * @return the entrypointport
	 */
	public Integer getEntrypointport() {
		return entrypointport;
	}

	/**
	 * @param entrypointport the entrypointport to set
	 */
	public void setEntrypointport(Integer entrypointport) {
		this.entrypointport = entrypointport;
	}

	/**
	 * @return the idxnode
	 */
	public Integer getIdxnode() {
		return idxnode;
	}

	/**
	 * @param idxnode the idxnode to set
	 */
	public void setIdxnode(Integer idxnode) {
		this.idxnode = idxnode;
	}

	/**
	 * @return the cycles
	 */
	public Integer getCycles() {
		return cycles;
	}

	/**
	 * @param cycles the cycles to set
	 */
	public void setCycles(Integer cycles) {
		this.cycles = cycles;
	}

	/**
	 * @return the requestvariation
	 */
	public Integer getRequestvariation() {
		return requestvariation;
	}

	/**
	 * @param requestvariation the requestvariation to set
	 */
	public void setRequestvariation(Integer requestvariation) {
		this.requestvariation = requestvariation;
	}

	/**
	 * @return the maxfreshness
	 */
	public Integer getMaxfreshness() {
		return maxfreshness;
	}

	/**
	 * @param maxfreshness the maxfreshness to set
	 */
	public void setMaxfreshness(Integer maxfreshness) {
		this.maxfreshness = maxfreshness;
	}

	/**
	 * @return the maxresponsetime
	 */
	public Integer getMaxresponsetime() {
		return maxresponsetime;
	}

	/**
	 * @param maxresponsetime the maxresponsetime to set
	 */
	public void setMaxresponsetime(Integer maxresponsetime) {
		this.maxresponsetime = maxresponsetime;
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

	/**
	 * @return the randomfreshness
	 */
	public boolean isRandomfreshness() {
		return randomfreshness;
	}

	/**
	 * @param randomfreshness the randomfreshness to set
	 */
	public void setRandomfreshness(boolean randomfreshness) {
		this.randomfreshness = randomfreshness;
	}

	/**
	 * @return the datatypeids
	 */
	public String[] getDatatypeids() {
		return datatypeids;
	}

	/**
	 * @param datatypeids the datatypeids to set
	 */
	public void setDatatypeids(String[] datatypeids) {
		this.datatypeids = datatypeids;
	}

	/**
	 * @return the idxdatatype
	 */
	public Integer getIdxdatatype() {
		return idxdatatype;
	}

	/**
	 * @param idxdatatype the idxdatatype to set
	 */
	public void setIdxdatatype(Integer idxdatatype) {
		this.idxdatatype = idxdatatype;
	}

	/**
	 * @return the collaboration
	 */
	public Collaboration getCollaboration() {
		return collaboration;
	}

	/**
	 * @param collaboration the collaboration to set
	 */
	public void setCollaboration(Collaboration collaboration) {
		this.collaboration = collaboration;
	}

	
}
