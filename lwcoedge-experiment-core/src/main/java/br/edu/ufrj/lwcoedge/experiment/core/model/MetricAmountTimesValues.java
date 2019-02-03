package br.edu.ufrj.lwcoedge.experiment.core.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MetricAmountTimesValues implements Serializable {

	private static final long serialVersionUID = -4814893896540372418L;

	public MetricAmountTimesValues() {}
	
	private ArrayList<MetricCTAmount> value;

	public ArrayList<MetricCTAmount> getValue() {
		return value;
	}
	
	public void setValue(ArrayList<MetricCTAmount> value) {
		this.value = value;
	}
}
