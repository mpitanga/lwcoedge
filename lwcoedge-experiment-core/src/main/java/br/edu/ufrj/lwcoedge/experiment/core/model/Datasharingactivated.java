package br.edu.ufrj.lwcoedge.experiment.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class Datasharingactivated implements Serializable {

	private static final long serialVersionUID = 2187253852751281307L;

	private Integer percentageofrequests;

	public Datasharingactivated() {}

	/**
	 * @return the percentageofrequests
	 */
	public Integer getPercentageofrequests() {
		return percentageofrequests;
	}

	/**
	 * @param percentageofrequests the percentageofrequests to set
	 */
	public void setPercentageofrequests(Integer percentageofrequests) {
		this.percentageofrequests = percentageofrequests;
	}

	@Override
	public String toString() {
		return Util.obj2json(this);
	}
}
