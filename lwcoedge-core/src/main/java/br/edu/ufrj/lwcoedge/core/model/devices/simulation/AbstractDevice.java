package br.edu.ufrj.lwcoedge.core.model.devices.simulation;

import java.io.Serializable;

public abstract class AbstractDevice implements Serializable {

	private static final long serialVersionUID = 146559416962879391L;

	private String hostName;
	private String ip;
	private String coordinates;

	private Integer memory;
	private Integer cpu;
	private Float watts;

	public AbstractDevice() {}
	
	public AbstractDevice(String hostName) {
		this.hostName = hostName;
	}
	
	abstract public Float getEnergy();

	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the coordinates
	 */
	public String getCoordinates() {
		return coordinates;
	}

	/**
	 * @param coordinates the coordinates to set
	 */
	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	/**
	 * @return the memory
	 */
	public Integer getMemory() {
		return memory;
	}

	/**
	 * @param memory the memory to set
	 */
	public void setMemory(Integer memory) {
		this.memory = memory;
	}

	/**
	 * @return the cpu
	 */
	public Integer getCpu() {
		return cpu;
	}

	/**
	 * @param cpu the cpu to set
	 */
	public void setCpu(Integer cpu) {
		this.cpu = cpu;
	}

	/**
	 * @return the watts
	 */
	public Float getWatts() {
		return watts;
	}

	/**
	 * @param watts the watts to set
	 */
	public void setWatts(Float watts) {
		this.watts = watts;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hostName == null) ? 0 : hostName.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractDevice other = (AbstractDevice) obj;
		if (hostName == null) {
			if (other.hostName != null)
				return false;
		} else if (!hostName.equals(other.hostName))
			return false;
		return true;
	}

}
