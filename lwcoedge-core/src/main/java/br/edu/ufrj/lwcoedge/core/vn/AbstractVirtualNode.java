package br.edu.ufrj.lwcoedge.core.vn;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.model.VirtualNode;
import br.edu.ufrj.lwcoedge.core.service.AbstractService;

public abstract class AbstractVirtualNode extends AbstractService implements Serializable {

	private static final long serialVersionUID = 4514113934710496810L;
	
	private VirtualNode vn;
	private boolean init = false;

	public AbstractVirtualNode() {}
	
/*	public AbstractVirtualNode(VirtualNode vn) {
		this.vn = vn;
	}
*/
	/**
	 * Method called when a VN is started
	 */
	abstract public void start();

	/**
	 * Method called when a VN is destroyed
	 */
	abstract public void stop();
	
	/**
	 * Method used to indicate whether a VN is running or not.
	 * @return A boolean value true (running) or false (not running). 
	 */
	abstract public boolean isRunning();
	
	/**
	 * @return the vn
	 */
	public VirtualNode getVn() {
		return vn;
	}

	/**
	 * @param vn the virtual node to set
	 */
	public void setVn(VirtualNode vn) {
		this.vn = vn;
	}

	/**
	 * @return the init
	 */
	public boolean getInit() {
		return init;
	}

	/**
	 * @param init the init to set
	 */
	public void setInit(boolean init) {
		this.init = init;
	}

	public String getUrl() {
		return this.vn.getHostName() + ":" + this.vn.getPort().toString();
	}

}
