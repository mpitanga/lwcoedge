package br.edu.ufrj.lwcoedge.experiment.db.entities;

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
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private int amountof;

	private String edgenode;

	private String experimentname;
	
	private String experimentcode;

	private String metric;

	private int valueof;

	private int variation;
	
	private String datatypeid;

	public Metricamountandvalue() {
	}


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * @return the amountof
	 */
	public int getAmountof() {
		return amountof;
	}


	/**
	 * @param amountof the amountof to set
	 */
	public void setAmountof(int amountof) {
		this.amountof = amountof;
	}


	/**
	 * @return the edgenode
	 */
	public String getEdgenode() {
		return edgenode;
	}


	/**
	 * @param edgenode the edgenode to set
	 */
	public void setEdgenode(String edgenode) {
		this.edgenode = edgenode;
	}


	/**
	 * @return the experimentname
	 */
	public String getExperimentname() {
		return experimentname;
	}


	/**
	 * @param experimentname the experimentname to set
	 */
	public void setExperimentname(String experimentname) {
		this.experimentname = experimentname;
	}


	/**
	 * @return the metric
	 */
	public String getMetric() {
		return metric;
	}


	/**
	 * @param metric the metric to set
	 */
	public void setMetric(String metric) {
		this.metric = metric;
	}


	/**
	 * @return the valueof
	 */
	public int getValueof() {
		return valueof;
	}


	/**
	 * @param valueof the valueof to set
	 */
	public void setValueof(int valueof) {
		this.valueof = valueof;
	}


	/**
	 * @return the variation
	 */
	public int getVariation() {
		return variation;
	}


	/**
	 * @param variation the variation to set
	 */
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