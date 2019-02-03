package entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the metricamount database table.
 * 
 */
@Entity
@Table(name="metricamount")
@NamedQuery(name="Metricamount.findAll", query="SELECT m FROM Metricamount m")
public class Metricamount implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private int amountof;

	private String edgenode;

	private String experimentname;

	private String metric;

	private int variation;

	public Metricamount() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAmountof() {
		return this.amountof;
	}

	public void setAmountof(int amountof) {
		this.amountof = amountof;
	}

	public String getEdgenode() {
		return this.edgenode;
	}

	public void setEdgenode(String edgenode) {
		this.edgenode = edgenode;
	}

	public String getExperimentname() {
		return this.experimentname;
	}

	public void setExperimentname(String experimentname) {
		this.experimentname = experimentname;
	}

	public String getMetric() {
		return this.metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public int getVariation() {
		return this.variation;
	}

	public void setVariation(int variation) {
		this.variation = variation;
	}

}