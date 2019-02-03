package entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


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
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	private String edgenode;

	private String experimentname;

	@Temporal(TemporalType.TIMESTAMP)
	private Date finish;

	private String metric;

	@Temporal(TemporalType.TIMESTAMP)
	private Date start;

	private int variation;

	public Metriccomputationtime() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Date getFinish() {
		return this.finish;
	}

	public void setFinish(Date finish) {
		this.finish = finish;
	}

	public String getMetric() {
		return this.metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public Date getStart() {
		return this.start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public int getVariation() {
		return this.variation;
	}

	public void setVariation(int variation) {
		this.variation = variation;
	}

}