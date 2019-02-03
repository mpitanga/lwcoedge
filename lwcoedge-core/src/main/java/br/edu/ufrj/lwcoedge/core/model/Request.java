package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

/**
 * @author mpalv
 *
 */
public class Request implements Serializable {

	private static final long serialVersionUID = -3995814122616851233L;
	
	private Datatype datatype;
	private Param param;
	private String callback;

	/**
	 * Default constructor
	 */
	public Request() {}

	/**
	 * @param datatype
	 * @param param
	 * @param callback
	 */
	public Request(Datatype datatype, Param param, String callback) {
		super();
		this.datatype = datatype;
		this.param = param;
		this.callback = callback;
	}

	public Datatype getDatatype() {
		return datatype;
	}

	public void setDatatype(Datatype datatype) {
		this.datatype = datatype;
	}

	public Param getParam() {
		return param;
	}

	public void setParam(Param param) {
		this.param = param;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}
	
}
