package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class Metrics implements Serializable {

	private static final long serialVersionUID = 8895472964458300832L;

	private Integer mem;
	private Integer memFree;
	private Integer numberOfProcessors;
	private Integer threads;
	private Integer threadsTotal;
	private Integer threadPeek;

	@JsonIgnore
	private boolean busy;
	
	public Metrics() {
		this.busy = false;
	}

	/**
	 * @param mem
	 * @param memFree
	 * @param numberOfProcessors
	 * @param threads
	 * @param threadsTotal
	 * @param threadPeek
	 */
	public Metrics(Integer mem, Integer memFree, Integer numberOfProcessors, Integer threads, Integer threadsTotal,
			Integer threadPeek) {
		super();
		this.mem = mem;
		this.memFree = memFree;
		this.numberOfProcessors = numberOfProcessors;
		this.threads = threads;
		this.threadsTotal = threadsTotal;
		this.threadPeek = threadPeek;
		this.busy = false;
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
	 * @return the memFree
	 */
	public Integer getMemFree() {
		return memFree;
	}

	/**
	 * @param memFree the memFree to set
	 */
	public void setMemFree(Integer memFree) {
		this.memFree = memFree;
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
	@JsonIgnore
	public boolean isResourceBusy() {
		return busy;
	}

	/**
	 * @param busy the busy to set
	 */
	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}
	
}
