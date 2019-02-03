package entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the metricamountandtimes database table.
 * 
 */
@Entity
@Table(name="metricamountandtimes")
@NamedQuery(name="Metricamountandtime.findAll", query="SELECT m FROM Metricamountandtime m")
public class Metricamountandtime implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private int amountof;

	private int computationinmillis;

	private int computationinseconds;

	private String edgenode;

	private String experimentname;

	private String metric;

	private int variation;

	public Metricamountandtime() {
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

	public int getComputationinmillis() {
		return this.computationinmillis;
	}

	public void setComputationinmillis(int computationinmillis) {
		this.computationinmillis = computationinmillis;
	}

	public int getComputationinseconds() {
		return this.computationinseconds;
	}

	public void setComputationinseconds(int computationinseconds) {
		this.computationinseconds = computationinseconds;
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