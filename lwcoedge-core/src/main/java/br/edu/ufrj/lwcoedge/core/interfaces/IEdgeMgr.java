package br.edu.ufrj.lwcoedge.core.interfaces;

import br.edu.ufrj.lwcoedge.core.model.Datatype;
import br.edu.ufrj.lwcoedge.core.model.Descriptor;
import br.edu.ufrj.lwcoedge.core.model.EdgeNode;
import br.edu.ufrj.lwcoedge.core.model.Resources;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;

public interface IEdgeMgr {

	public boolean hasResource(Datatype datatype, String... args) throws Exception;
	public boolean hasConnectedDevices(Descriptor datatype, String... args);
	public VirtualNode containerDeploy(Datatype datatype, String... args) throws Exception;
	public void containerStop(VirtualNode vn, String... args);
	public boolean scaleUp(VirtualNode vn, String... args);
	public boolean scaleDown(VirtualNode vn, String... args);
	public Object getMinimalResources();
	public Resources getNeededResources(String type);
	public EdgeNode getEdgeNode();
	public void loadEdgenodeConfig() throws Exception;
}
