package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class Metrics implements Serializable {

	private static final long serialVersionUID = 8895472964458300832L;

	private Integer mem;
	private Integer memUsed;
	private Integer memFree;
	private String unit;
	private Integer numberOfProcessors;
	private Integer threads;
	private Integer threadsTotal;
	private Integer threadPeek;
	private boolean resourceBusy;
	
	public Metrics() {
		this.unit = "K";
	}

	/**
	 * @return the mem
	 */
	public Integer getMem() {
		return mem;
	}

	/**
	 * @param mem the mem to set
	 */
	public void setMem(Integer mem) {
		this.mem = mem;
	}

	/**
	 * @return the numberOfProcessors
	 */
	public Integer getNumberOfProcessors() {
		return numberOfProcessors;
	}

	/**
	 * @param numberOfProcessors the numberOfProcessors to set
	 */
	public void setNumberOfProcessors(Integer numberOfProcessors) {
		this.numberOfProcessors = numberOfProcessors;
	}

	/**
	 * @return the threads
	 */
	public Integer getThreads() {
		return threads;
	}

	/**
	 * @param threads the threads to set
	 */
	public void setThreads(Integer threads) {
		this.threads = threads;
	}

	/**
	 * @return the threadsTotal
	 */
	public Integer getThreadsTotal() {
		return threadsTotal;
	}

	/**
	 * @param threadsTotal the threadsTotal to set
	 */
	public void setThreadsTotal(Integer threadsTotal) {
		this.threadsTotal = threadsTotal;
	}

	/**
	 * @return the threadPeek
	 */
	public Integer getThreadPeek() {
		return threadPeek;
	}

	/**
	 * @param threadPeek the threadPeek to set
	 */
	public void setThreadPeek(Integer threadPeek) {
		this.threadPeek = threadPeek;
	}

	/**
	 * @return the busy
	 */
	public boolean isResourceBusy() {
		return this.resourceBusy;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}

	/**
	 * @return the memUsed
	 */
	public Integer getMemUsed() {
		return memUsed;
	}

	/**
	 * @param memUsed the memUsed to set
	 */
	public void setMemUsed(Integer memUsed) {
		this.memUsed = memUsed;
	}

	/**
	 * @param resourceBusy the resourceBusy to set
	 */
	public void setResourceBusy(boolean resourceBusy) {
		this.resourceBusy = resourceBusy;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getMemFree() {
		return memFree;
	}

	public void setMemFree(Integer memFree) {
		this.memFree = memFree;
	}
		
	public int compare(Object obj) {
		if (obj == null || this == obj)
			return 0;

		Metrics other = (Metrics) obj;

		if (numberOfProcessors >= other.numberOfProcessors) {
			double memFree1 = Util.calcPercentage(mem, memUsed, 0);
			double memFree2 = Util.calcPercentage(other.mem, other.memUsed, 0);
			if (memFree1 > memFree2) { //+ available memory
				return 1;
			} else if (memFree1 < memFree2) {
				return -1;
			} else if (memFree1 == memFree2) {
				return 0;
			}
		}
		return -1;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((memFree == null) ? 0 : memFree.hashCode());
		result = prime * result + ((numberOfProcessors == null) ? 0 : numberOfProcessors.hashCode());
		return result;
	}

	public boolean equalsResources(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Metrics other = (Metrics) obj;
		if (memFree == null) {
			if (other.memFree != null)
				return false;
		} else if (!memFree.equals(other.memFree))
			return false;
		if (numberOfProcessors == null) {
			if (other.numberOfProcessors != null)
				return false;
		} else if (!numberOfProcessors.equals(other.numberOfProcessors))
			return false;
		return true;
	}

/*	public static void main(String[] args) {
		Metrics m1 = new Metrics();
		m1.mem    = 101888;
		m1.memUsed = 92000;
		m1.memFree = m1.mem - m1.memUsed;
		m1.numberOfProcessors = 2;
		
		Metrics m2 = new Metrics();
		m2.mem    = 101888;
		m2.memUsed = 91000;
		m2.memFree = m2.mem - m2.memUsed;
		m2.numberOfProcessors = 2;

		System.out.println(m1.equalsResources(m2));
		System.out.println(m1.compare(m2));
	}
*/
}
