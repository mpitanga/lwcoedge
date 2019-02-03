package br.edu.ufrj.lwcoedge.experiment.core.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MetricAmountAndTimesValues implements Serializable {

	private static final long serialVersionUID = -4814893896540372418L;

	public MetricAmountAndTimesValues() {}
	
	private ArrayList<MetricCTAndAmount> value;

	public ArrayList<MetricCTAndAmount> getValue() {
		return value;
	}
	
	public void setValue(ArrayList<MetricCTAndAmount> value) {
		this.value = value;
	}
}
