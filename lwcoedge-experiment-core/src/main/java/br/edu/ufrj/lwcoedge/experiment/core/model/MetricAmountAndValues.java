package br.edu.ufrj.lwcoedge.experiment.core.model;

import java.io.Serializable;
import java.util.ArrayList;

import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmountAndValue;

public class MetricAmountAndValues implements Serializable {

	private static final long serialVersionUID = -1433510307978147098L;

	public MetricAmountAndValues() {}

	private ArrayList<MetricAmountAndValue> value;

	public ArrayList<MetricAmountAndValue> getValue() {
		return value;
	}
	public void setValue(ArrayList<MetricAmountAndValue> value) {
		this.value = value;
	}

}
