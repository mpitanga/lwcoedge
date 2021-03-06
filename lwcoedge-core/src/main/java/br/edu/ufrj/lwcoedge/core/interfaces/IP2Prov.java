package br.edu.ufrj.lwcoedge.core.interfaces;

import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;

public interface IP2Prov extends IAppConfig {
	public void sendToNeighborNode(Request request, String... args) throws Exception;
	public void registerVNtoDataSharing(VirtualNode newVirtualNode, String... args) throws Exception;
	// auxiliar
	public void setCollaboration(boolean enable);
	public void edgeNodeConfig() throws Exception;
	public void neededResources() throws Exception;
}
