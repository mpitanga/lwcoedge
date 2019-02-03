package br.edu.ufrj.lwcoedge.experiment.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class ExperimentLoadToDBConfig implements Serializable {

	private static final long serialVersionUID = 1832432862550538800L;
	
    private String experimentname;
	private String basepath;
    private String[] edgenodes;
    private Integer idxnode;    
    private Integer times;
    private Integer cycles;
    private Integer requestvariation;
       
	public ExperimentLoadToDBConfig() {}

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

	@Override
	public String toString() {
		return Util.obj2json(this);
	}
}
