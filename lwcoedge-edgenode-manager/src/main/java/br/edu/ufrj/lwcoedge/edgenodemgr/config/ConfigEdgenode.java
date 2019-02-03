package br.edu.ufrj.lwcoedge.edgenodemgr.config;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.model.EdgeNode;

public class ConfigEdgenode implements Serializable {

	private static final long serialVersionUID = -9047884471062120134L;

	private EdgeNode edgeNode;
	
	public ConfigEdgenode() {}

	public EdgeNode getEdgeNode() {
		return edgeNode;
	}

	public void setEdgeNode(EdgeNode edgeNode) {
		this.edgeNode = edgeNode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ConfigEdgenode [edgeNode=");
		builder.append(edgeNode);
		builder.append("]");
		return builder.toString();
	}

}
