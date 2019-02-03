package br.edu.ufrj.lwcoedge.core.interfaces;

import br.edu.ufrj.lwcoedge.core.model.Metrics;
import br.edu.ufrj.lwcoedge.core.model.ResourcesAvailable;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;

public interface IMonitor {

	public Metrics getVirtualNodeMetrics(VirtualNode vn, String... args) throws Exception;
	public ResourcesAvailable getNodeResources(String... args) throws Exception;
}
