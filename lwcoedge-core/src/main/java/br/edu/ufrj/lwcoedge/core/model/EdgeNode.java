package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;
import java.util.ArrayList;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class EdgeNode implements Serializable {

	private static final long serialVersionUID = -5779025558498286737L;

	private String hostName;
	private String ip;
	private String coordinates;

	private ArrayList<ConnectedDevices> connectedDevices = new ArrayList<ConnectedDevices>();
	private ArrayList<EdgeNode> neighborhood = new ArrayList<EdgeNode>();
	
	public EdgeNode() {}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public ArrayList<ConnectedDevices> getConnectedDevices() {
		return connectedDevices;
	}

	public void setConnectedDevices(ArrayList<ConnectedDevices> connectedDevices) {
		this.connectedDevices = connectedDevices;
	}

	public ArrayList<EdgeNode> getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(ArrayList<EdgeNode> neighborhood) {
		this.neighborhood = neighborhood;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}

}
