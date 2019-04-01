package br.edu.ufrj.lwcoedge.vn.sensing;

import br.edu.ufrj.lwcoedge.core.interfaces.INeighbor;
import br.edu.ufrj.lwcoedge.core.interfaces.IReceive;
import br.edu.ufrj.lwcoedge.core.interfaces.IRequest;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;

public interface IVNSensingService extends IRequest, INeighbor, IReceive {
	public boolean isRunning();
	public VirtualNode getVn();	
}
