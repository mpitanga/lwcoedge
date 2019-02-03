package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;
import java.util.ArrayList;

public class VirtualNodeInstances implements Serializable {

	private static final long serialVersionUID = 2257556300850923557L;

	ArrayList<VirtualNode> virtualNodeInstances;
	public VirtualNodeInstances() {}
	/**
	 * @return the virtualNodeInstances
	 */
	public ArrayList<VirtualNode> getVirtualNodeInstances() {
		return virtualNodeInstances;
	}
	/**
	 * @param virtualNodeInstances the virtualNodeInstances to set
	 */
	public void setVirtualNodeInstances(ArrayList<VirtualNode> virtualNodeInstances) {
		this.virtualNodeInstances = virtualNodeInstances;
	}

}
