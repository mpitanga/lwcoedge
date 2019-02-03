package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class ResourcesAvailable implements Serializable {

	private static final long serialVersionUID = -871000512062117485L;

	private Long totalPhysicalMemorySize;
	private Long freePhysicalMemorySize;
	private Long cpu;
	
	public ResourcesAvailable() {}

	/**
	 * @param totalPhysicalMemorySize
	 * @param freePhysicalMemorySize
	 * @param cpu
	 */
	public ResourcesAvailable(Long totalPhysicalMemorySize, Long freePhysicalMemorySize, Long cpu) {
		super();
		this.totalPhysicalMemorySize = totalPhysicalMemorySize;
		this.freePhysicalMemorySize = freePhysicalMemorySize;
		this.cpu = cpu;
	}

	/**
	 * @return the totalPhysicalMemorySize
	 */
	public Long getTotalPhysicalMemorySize() {
		return totalPhysicalMemorySize;
	}

	/**
	 * @param totalPhysicalMemorySize the totalPhysicalMemorySize to set
	 */
	public void setTotalPhysicalMemorySize(Long totalPhysicalMemorySize) {
		this.totalPhysicalMemorySize = totalPhysicalMemorySize;
	}

	/**
	 * @return the freePhysicalMemorySize
	 */
	public Long getFreePhysicalMemorySize() {
		return freePhysicalMemorySize;
	}

	/**
	 * @param freePhysicalMemorySize the freePhysicalMemorySize to set
	 */
	public void setFreePhysicalMemorySize(Long freePhysicalMemorySize) {
		this.freePhysicalMemorySize = freePhysicalMemorySize;
	}

	/**
	 * @return the cpu
	 */
	public Long getCpu() {
		return cpu;
	}

	/**
	 * @param cpu the cpu to set
	 */
	public void setCpu(Long cpu) {
		this.cpu = cpu;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}

}
