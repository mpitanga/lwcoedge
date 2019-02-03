package br.edu.ufrj.lwcoedge.experiment.db.entities;

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
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private int amountof;

	private String edgenode;

	private String experimentname;

	private String experimentcode;

	private String metric;

	private int variation;
	
	private String datatypeid;

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

	/**
	 * @return the datatypeid
	 */
	public String getDatatypeid() {
		return datatypeid;
	}

	/**
	 * @param datatypeid the datatypeid to set
	 */
	public void setDatatypeid(String datatypeid) {
		this.datatypeid = datatypeid;
	}

	/**
	 * @return the experimentcode
	 */
	public String getExperimentcode() {
		return experimentcode;
	}

	/**
	 * @param experimentcode the experimentcode to set
	 */
	public void setExperimentcode(String experimentcode) {
		this.experimentcode = experimentcode;
	}

}