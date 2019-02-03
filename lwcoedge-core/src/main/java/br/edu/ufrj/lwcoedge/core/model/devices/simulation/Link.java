package br.edu.ufrj.lwcoedge.core.model.devices.simulation;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class Link implements Serializable {

	private static final long serialVersionUID = -9095826611655575020L;

	private AbstractDevice destination;
	private Long bw;
	private Long pr;
	

	public Link() {}
	public Link(AbstractDevice destination, Long bw, Long pr) {
		super();
		this.destination = destination;
		this.bw = bw;
		this.pr = pr;
	}

	/**
	 * Latency, based on YAFS model
	 * @param messageSize message size in bits
	 * @return
	 */
	public Float getLatency(Long messageSize) {
        Float transmit = (float) (messageSize/(this.bw* 1000000.0)); //mbits
		return transmit + this.pr;
	}
	/**
	 * Channel Bandwidth: in Bytes.
	 * @return the bw
	 */
	public Long getBw() {
		return bw;
	}
	/**
	 * @param bw the bw to set
	 */
	public void setBw(Long bw) {
		this.bw = bw;
	}
	/**
	 * Channel Propagation speed
	 * @return the pr 
	 */
	public Long getPr() {
		return pr;
	}
	/**
	 * @param pr the pr to set
	 */
	public void setPr(Long pr) {
		this.pr = pr;
	}
	/**
	 * @return the destination
	 */
	public AbstractDevice getDestination() {
		return destination;
	}
	/**
	 * @param destination the destination to set
	 */
	public void setDestination(AbstractDevice destination) {
		this.destination = destination;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}

}
