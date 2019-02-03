package br.edu.ufrj.lwcoedge.core.metrics.experiment;

import java.io.Serializable;

public abstract class AbstractMetric implements Serializable {

	private static final long serialVersionUID = -5010942121883398714L;
	
	private String id;

	public AbstractMetric() {}
	
	public AbstractMetric(String id) {
		super();
		this.id = id;
	}

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

}
