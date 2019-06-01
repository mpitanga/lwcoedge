package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class ResourcesAvailable implements Serializable {

	private static final long serialVersionUID = -871000512062117485L;

	private Long totalPhysicalMemorySize;
	private Long freePhysicalMemorySize;
	private String unit;
	private Long cpu;
	
	public ResourcesAvailable() {}

	/**
	 * @param totalPhysicalMemorySize
	 * @param freePhysicalMemorySize
	 * @param cpu
	 */
	public ResourcesAvailable(Long totalPhysicalMemorySize, Long freePhysicalMemorySize, Long cpu, String unit) {
		super();
		this.totalPhysicalMemorySize = totalPhysicalMemorySize;
		this.freePhysicalMemorySize = freePhysicalMemorySize;
		this.cpu = cpu;
		this.unit = unit;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cpu == null) ? 0 : cpu.hashCode());
		result = prime * result + ((freePhysicalMemorySize == null) ? 0 : freePhysicalMemorySize.hashCode());
		result = prime * result + ((totalPhysicalMemorySize == null) ? 0 : totalPhysicalMemorySize.hashCode());
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
		ResourcesAvailable other = (ResourcesAvailable) obj;
		if (cpu == null) {
			if (other.cpu != null)
				return false;
		} else if (!cpu.equals(other.cpu))
			return false;
		if (freePhysicalMemorySize == null) {
			if (other.freePhysicalMemorySize != null)
				return false;
		} else if (!freePhysicalMemorySize.equals(other.freePhysicalMemorySize))
			return false;
		if (totalPhysicalMemorySize == null) {
			if (other.totalPhysicalMemorySize != null)
				return false;
		} else if (!totalPhysicalMemorySize.equals(other.totalPhysicalMemorySize))
			return false;
		return true;
	}

	public int compare(Object obj) {
		if (obj == null || this == obj)
			return 0;

		ResourcesAvailable other = (ResourcesAvailable) obj;

		if (cpu >= other.cpu) {
			double memFree1 = 100-Util.calcPercentage (totalPhysicalMemorySize, freePhysicalMemorySize, 0);
			double memFree2 = 100-Util.calcPercentage (other.totalPhysicalMemorySize, other.freePhysicalMemorySize, 0);
			if (memFree1 > memFree2) {
				return -1;
			} else if (memFree1 < memFree2) {
				return 1;
			} else if (memFree1 == memFree2) {
				return 0;
			}
		}
		return -1;
	}
	
	@JsonIgnore
	public double memoryAvailable() {
		return 100-Util.calcPercentage (totalPhysicalMemorySize, freePhysicalMemorySize, 0);
	}
	
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public static void main(String[] args) {
		ResourcesAvailable ra1 = new ResourcesAvailable();
		ra1.unit = "K";
		ra1.cpu = 2l;
		ra1.totalPhysicalMemorySize = 1024l;
		ra1.freePhysicalMemorySize = 100l;
		System.out.println(ra1.memoryAvailable());
		
		ResourcesAvailable ra2 = new ResourcesAvailable();
		ra2.unit = "K";
		ra2.cpu = 2l;
		ra2.totalPhysicalMemorySize = 1000l;
		ra2.freePhysicalMemorySize = 700l;
		System.out.println(ra2.memoryAvailable());

		System.out.println(ra1.compare(ra2));
	}

}
