package br.edu.ufrj.lwcoedge.experiment.submit.db.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.edu.ufrj.lwcoedge.core.util.Util;


/**
 * The persistent class for the requests database table.
 * 
 */
@Entity
@Table(name="requests")
public class Requests implements Serializable {

	private static final long serialVersionUID = -3751345448995146681L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String experimentname;
	private String experimentcode;
	private int variation;
	private String edgenode;
	private String request;
	private boolean activatecollaboration;
	private boolean activatedatasharing;
	private int idxhost;
	private int experimentvar;
	private String experimentid;
	private String url;
	
	public Requests() {}

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
	 * @return the request
	 */
	public String getRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(String request) {
		this.request = request;
	}

	/**
	 * @return the activatecollaboration
	 */
	public boolean isActivatecollaboration() {
		return activatecollaboration;
	}

	/**
	 * @param activatecollaboration the activatecollaboration to set
	 */
	public void setActivatecollaboration(boolean activatecollaboration) {
		this.activatecollaboration = activatecollaboration;
	}

	/**
	 * @return the activatedatasharing
	 */
	public boolean isActivatedatasharing() {
		return activatedatasharing;
	}

	/**
	 * @param activatedatasharing the activatedatasharing to set
	 */
	public void setActivatedatasharing(boolean activatedatasharing) {
		this.activatedatasharing = activatedatasharing;
	}

	/**
	 * @return the idxhost
	 */
	public int getIdxhost() {
		return idxhost;
	}

	/**
	 * @param idxhost the idxhost to set
	 */
	public void setIdxhost(int idxhost) {
		this.idxhost = idxhost;
	}

	/**
	 * @return the experimentvar
	 */
	public int getExperimentvar() {
		return experimentvar;
	}

	/**
	 * @param experimentvar the experimentvar to set
	 */
	public void setExperimentvar(int experimentvar) {
		this.experimentvar = experimentvar;
	}

	/**
	 * @return the experimentid
	 */
	public String getExperimentid() {
		return experimentid;
	}

	/**
	 * @param experimentid the experimentid to set
	 */
	public void setExperimentid(String experimentid) {
		this.experimentid = experimentid;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return Util.obj2json(this);
	}
}