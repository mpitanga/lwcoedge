package br.edu.ufrj.lwcoedge.experiment.db.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the metriccomputationtime database table.
 * 
 */
@Entity
@Table(name="metriccomputationtime")
@NamedQuery(name="Metriccomputationtime.findAll", query="SELECT m FROM Metriccomputationtime m")
public class Metriccomputationtime implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String edgenode;

	private String experimentname;

	private String experimentcode;

	private String start;
	private String finish;

	private String metric;

	private int variation;
	
	private String datatypeid;

	private int computationinmillis;
	private int computationinseconds;

	public Metriccomputationtime() {
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

	/**
	 * @return the start
	 */
	public String getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(String start) {
		this.start = start;
	}

	/**
	 * @return the finish
	 */
	public String getFinish() {
		return finish;
	}

	/**
	 * @param finish the finish to set
	 */
	public void setFinish(String finish) {
		this.finish = finish;
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
	 * @return the computationinmillis
	 */
	public int getComputationinmillis() {
		return computationinmillis;
	}

	/**
	 * @param computationinmillis the computationinmillis to set
	 */
	public void setComputationinmillis(int computationinmillis) {
		this.computationinmillis = computationinmillis;
	}

	/**
	 * @return the computationinseconds
	 */
	public int getComputationinseconds() {
		return computationinseconds;
	}

	/**
	 * @param computationinseconds the computationinseconds to set
	 */
	public void setComputationinseconds(int computationinseconds) {
		this.computationinseconds = computationinseconds;
	}

}