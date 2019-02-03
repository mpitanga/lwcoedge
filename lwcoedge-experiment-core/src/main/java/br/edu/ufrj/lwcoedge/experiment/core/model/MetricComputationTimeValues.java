package br.edu.ufrj.lwcoedge.experiment.core.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MetricComputationTimeValues implements Serializable {

	private static final long serialVersionUID = -9081949153029205293L;

	public MetricComputationTimeValues() {}
	
	private ArrayList<MetricCT> value;
	
	public ArrayList<MetricCT> getValue() {
		return value;
	}
	
	public void setValue(ArrayList<MetricCT> value) {
		this.value = value;
	}

}
