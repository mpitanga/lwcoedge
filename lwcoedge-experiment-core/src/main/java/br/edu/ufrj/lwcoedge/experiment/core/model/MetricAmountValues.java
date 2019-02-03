package br.edu.ufrj.lwcoedge.experiment.core.model;

import java.io.Serializable;
import java.util.ArrayList;

import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmount;

public class MetricAmountValues implements Serializable {

	private static final long serialVersionUID = -1433510307978147098L;

	public MetricAmountValues() {}

	private ArrayList<MetricAmount> value;

	public ArrayList<MetricAmount> getValue() {
		return value;
	}
	public void setValue(ArrayList<MetricAmount> value) {
		this.value = value;
	}

}
