package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class DataSharing implements Serializable {

	private static final long serialVersionUID = -8034243379292121882L;

	private VirtualNode virtualNode;
	private DataToShare ds;
	
	public DataSharing() {}

	/**
	 * @return the virtualNode
	 */
	public VirtualNode getVirtualNode() {
		return virtualNode;
	}

	/**
	 * @param virtualNode the virtualNode to set
	 */
	public void setVirtualNode(VirtualNode virtualNode) {
		this.virtualNode = virtualNode;
	}

	/**
	 * @return the ds
	 */
	public DataToShare getDs() {
		return ds;
	}

	/**
	 * @param ds the ds to set
	 */
	public void setDs(DataToShare ds) {
		this.ds = ds;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}

	
}
