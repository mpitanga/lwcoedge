package br.edu.ufrj.lwcoedge.experiment.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class Collaboration implements Serializable {

	private static final long serialVersionUID = 6536756758622071108L;

	private Datasharingactivated datasharingactivated;
	private boolean active;
    
	public Collaboration() {}

	/**
	 * @return the datasharingactivated
	 */
	public Datasharingactivated getDatasharingactivated() {
		return datasharingactivated;
	}

	/**
	 * @param datasharingactivated the datasharingactivated to set
	 */
	public void setDatasharingactivated(Datasharingactivated datasharingactivated) {
		this.datasharingactivated = datasharingactivated;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return Util.obj2json(this);
	}
}
