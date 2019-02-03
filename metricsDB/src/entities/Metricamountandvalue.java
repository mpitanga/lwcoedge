package entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the metricamountandvalue database table.
 * 
 */
@Entity
@Table(name="metricamountandvalue")
@NamedQuery(name="Metricamountandvalue.findAll", query="SELECT m FROM Metricamountandvalue m")
public class Metricamountandvalue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private int amountOf;

	private String edgeNode;

	private String experimentName;

	private String metric;

	private int valueOf;

	private int variation;

	public Metricamountandvalue() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAmountOf() {
		return this.amountOf;
	}

	public void setAmountOf(int amountOf) {
		this.amountOf = amountOf;
	}

	public String getEdgeNode() {
		return this.edgeNode;
	}

	public void setEdgeNode(String edgeNode) {
		this.edgeNode = edgeNode;
	}

	public String getExperimentName() {
		return this.experimentName;
	}

	public void setExperimentName(String experimentName) {
		this.experimentName = experimentName;
	}

	public String getMetric() {
		return this.metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public int getValueOf() {
		return this.valueOf;
	}

	public void setValueOf(int valueOf) {
		this.valueOf = valueOf;
	}

	public int getVariation() {
		return this.variation;
	}

	public void setVariation(int variation) {
		this.variation = variation;
	}

}