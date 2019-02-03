package br.edu.ufrj.lwcoedge.core.interfaces;

import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;

public interface IProvisioning {

	public VirtualNode provisioning(VirtualNode currentVirtualNode, Request request, String... args) throws Exception;
}
