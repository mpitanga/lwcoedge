/**
 * 
 */
package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

/**
 * @author mpalv
 *
 */
public class Param implements Serializable {
	private static final long serialVersionUID = -8623554555380497693L;
	
	private Integer sr;
	private Integer fr;
	private Integer rtt;
	
	/**
	 * Default constructor.
	 */
	public Param() {}

	/**
	 * @param fr  The data freshness threshold.
	 * @param rtt The response time threshold.
	 * @param sr  The data sample rating.
	 */
	public Param(Integer fr, Integer rtt, Integer sr) {
		super();
		this.fr = fr;
		this.sr = sr;
		this.rtt = rtt;
	}

	/**
	 * @return the sr
	 */
	public Integer getSr() {
		return sr;
	}

	/**
	 * @param sr the sr to set
	 */
	public void setSr(Integer sr) {
		this.sr = sr;
	}

	/**
	 * @return the fr
	 */
	public Integer getFr() {
		return fr;
	}

	/**
	 * @param fr the fr to set
	 */
	public void setFr(Integer fr) {
		this.fr = fr;
	}

	/**
	 * @return the rtt
	 */
	public Integer getRtt() {
		return rtt;
	}

	/**
	 * @param rtt the rtt to set
	 */
	public void setRtt(Integer rtt) {
		this.rtt = rtt;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}

}
