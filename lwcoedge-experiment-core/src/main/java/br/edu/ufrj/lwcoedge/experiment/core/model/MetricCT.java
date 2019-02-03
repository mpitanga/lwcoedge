package br.edu.ufrj.lwcoedge.experiment.core.model;

import java.io.Serializable;

public class MetricCT implements Serializable {

	private static final long serialVersionUID = -1691155279219432915L;
	
	private String id;
	private String start;
	private String finish;
	private Long computationinMillis;
	private Long computationinSeconds;
	
	public MetricCT() {}

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
	 * @return the start
	 */
	public String getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(String start) {
		this.start = start;
	}

	/**
	 * @return the finish
	 */
	public String getFinish() {
		return finish;
	}

	/**
	 * @param finish the finish to set
	 */
	public void setFinish(String finish) {
		this.finish = finish;
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

}
