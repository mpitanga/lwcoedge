package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class ResourceProvisioningParams implements Serializable {

	private static final long serialVersionUID = -9074267395725721014L;
	
	private VirtualNode currentVirtualNode;
	private Request request;
	
	public ResourceProvisioningParams() {}

	public VirtualNode getCurrentVirtualNode() {
		return currentVirtualNode;
	}

	public void setCurrentVirtualNode(VirtualNode currentVirtualNode) {
		this.currentVirtualNode = currentVirtualNode;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	@Override
	public String toString() {
		return Util.obj2json(this);
	}

}
