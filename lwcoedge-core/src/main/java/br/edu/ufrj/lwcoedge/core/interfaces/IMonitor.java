package br.edu.ufrj.lwcoedge.core.interfaces;

import br.edu.ufrj.lwcoedge.core.model.Metrics;
import br.edu.ufrj.lwcoedge.core.model.ResourcesAvailable;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;

public interface IMonitor extends IAppConfig {

	public Metrics getVirtualNodeMetrics(VirtualNode vn, String... args) throws Exception;
	public ResourcesAvailable getNodeResources(String... args) throws Exception;
	// auxiliar methods
	public void setPercentualMinMemAvailable(double value);
}
