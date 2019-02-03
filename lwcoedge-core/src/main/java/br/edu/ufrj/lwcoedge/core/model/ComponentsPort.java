package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class ComponentsPort implements Serializable {

	private static final long serialVersionUID = 5832922208612598846L;

	private Integer lwcoedge_vn_sensing; 
	private Integer lwcoedge_vn_actuation;
	private Integer lwcoedge_vn_datahandling;
	private Integer lwcoedge_vn_instancecache;
	private Integer lwcoedge_edgenode_manager;
	private Integer lwcoedge_resourceprovisioner;
	private Integer lwcoedge_resourceallocator;
	private Integer lwcoedge_catalog_manager;
	private Integer lwcoedge_monitor;
	private Integer lwcoedge_p2pcollaboration;
	private Integer lwcoedge_p2pdatasharing;
	private Integer lwcoedge_manager_api;

	public ComponentsPort() {}

	public Integer getLwcoedge_vn_sensing() {
		return lwcoedge_vn_sensing;
	}

	public void setLwcoedge_vn_sensing(Integer lwcoedge_vn_sensing) {
		this.lwcoedge_vn_sensing = lwcoedge_vn_sensing;
	}

	public Integer getLwcoedge_vn_actuation() {
		return lwcoedge_vn_actuation;
	}

	public void setLwcoedge_vn_actuation(Integer lwcoedge_vn_actuation) {
		this.lwcoedge_vn_actuation = lwcoedge_vn_actuation;
	}

	public Integer getLwcoedge_vn_datahandling() {
		return lwcoedge_vn_datahandling;
	}

	public void setLwcoedge_vn_datahandling(Integer lwcoedge_vn_datahandling) {
		this.lwcoedge_vn_datahandling = lwcoedge_vn_datahandling;
	}

	public Integer getLwcoedge_vn_instancecache() {
		return lwcoedge_vn_instancecache;
	}

	public void setLwcoedge_vn_instancecache(Integer lwcoedge_vn_instancecache) {
		this.lwcoedge_vn_instancecache = lwcoedge_vn_instancecache;
	}

	public Integer getLwcoedge_edgenode_manager() {
		return lwcoedge_edgenode_manager;
	}

	public void setLwcoedge_edgenode_manager(Integer lwcoedge_edgenode_manager) {
		this.lwcoedge_edgenode_manager = lwcoedge_edgenode_manager;
	}

	public Integer getLwcoedge_resourceprovisioner() {
		return lwcoedge_resourceprovisioner;
	}

	public void setLwcoedge_resourceprovisioner(Integer lwcoedge_resourceprovisioner) {
		this.lwcoedge_resourceprovisioner = lwcoedge_resourceprovisioner;
	}

	public Integer getLwcoedge_resourceallocator() {
		return lwcoedge_resourceallocator;
	}

	public void setLwcoedge_resourceallocator(Integer lwcoedge_resourceallocator) {
		this.lwcoedge_resourceallocator = lwcoedge_resourceallocator;
	}

	public Integer getLwcoedge_catalog_manager() {
		return lwcoedge_catalog_manager;
	}

	public void setLwcoedge_catalog_manager(Integer lwcoedge_catalog_manager) {
		this.lwcoedge_catalog_manager = lwcoedge_catalog_manager;
	}

	public Integer getLwcoedge_monitor() {
		return lwcoedge_monitor;
	}

	public void setLwcoedge_monitor(Integer lwcoedge_monitor) {
		this.lwcoedge_monitor = lwcoedge_monitor;
	}

	/**
	 * @return the lwcoedge_p2pcollaboration
	 */
	public Integer getLwcoedge_p2pcollaboration() {
		return lwcoedge_p2pcollaboration;
	}

	/**
	 * @param lwcoedge_p2pcollaboration the lwcoedge_p2pcollaboration to set
	 */
	public void setLwcoedge_p2pcollaboration(Integer lwcoedge_p2pcollaboration) {
		this.lwcoedge_p2pcollaboration = lwcoedge_p2pcollaboration;
	}

	/**
	 * @return the lwcoedge_p2pdatasharing
	 */
	public Integer getLwcoedge_p2pdatasharing() {
		return lwcoedge_p2pdatasharing;
	}

	/**
	 * @param lwcoedge_p2pdatasharing the lwcoedge_p2pdatasharing to set
	 */
	public void setLwcoedge_p2pdatasharing(Integer lwcoedge_p2pdatasharing) {
		this.lwcoedge_p2pdatasharing = lwcoedge_p2pdatasharing;
	}

	
	/**
	 * @return the lwcoedge_manager_api
	 */
	public Integer getLwcoedge_manager_api() {
		return lwcoedge_manager_api;
	}

	/**
	 * @param lwcoedge_manager_api the lwcoedge_manager_api to set
	 */
	public void setLwcoedge_manager_api(Integer lwcoedge_manager_api) {
		this.lwcoedge_manager_api = lwcoedge_manager_api;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}

}
