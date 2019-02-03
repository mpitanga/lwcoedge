package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class Resources implements Serializable {

	private static final long serialVersionUID = 2329951256628870344L;

	private Long physicalMemorySize;
	private Long physicalCpuNumbers;
	private String unit;
	
	public Resources() {}
	/**
	 * @param physicalMemorySize
	 * @param physicalCpuNumbers
	 * @param unit
	 */
	public Resources(Long physicalMemorySize, Long physicalCpuNumbers, String unit) {
		super();
		this.physicalMemorySize = physicalMemorySize;
		this.physicalCpuNumbers = physicalCpuNumbers;
		this.unit = unit;
	}
	/**
	 * @return the physicalMemorySize
	 */
	public Long getPhysicalMemorySize() {
		return physicalMemorySize;
	}
	/**
	 * @param physicalMemorySize the physicalMemorySize to set
	 */
	public void setPhysicalMemorySize(Long physicalMemorySize) {
		this.physicalMemorySize = physicalMemorySize;
	}
	/**
	 * @return the physicalCpuNumbers
	 */
	public Long getPhysicalCpuNumbers() {
		return physicalCpuNumbers;
	}
	/**
	 * @param physicalCpuNumbers the physicalCpuNumbers to set
	 */
	public void setPhysicalCpuNumbers(Long physicalCpuNumbers) {
		this.physicalCpuNumbers = physicalCpuNumbers;
	}	
	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}
	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}
}
