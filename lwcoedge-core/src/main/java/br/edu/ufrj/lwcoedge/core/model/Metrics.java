package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class Metrics implements Serializable {

	private static final long serialVersionUID = 8895472964458300832L;

	private Integer mem;
	private Integer memUsed;
	private Integer numberOfProcessors;
	private Integer threads;
	private Integer threadsTotal;
	private Integer threadPeek;
	private boolean resourceBusy;
	
	public Metrics() {}

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
	
}
