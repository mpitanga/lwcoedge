package br.edu.ufrj.lwcoedge.context.broker.elements;

import java.io.Serializable;
import java.util.List;

public class ContextBrokerResponse implements Serializable {

	private static final long serialVersionUID = 6727459336515950784L;

	private List<ContextResponses> contextResponses;
	 
	public ContextBrokerResponse() {}

	/**
	 * @return the contextResponses
	 */
	public List<ContextResponses> getContextResponses() {
		return contextResponses;
	}

	/**
	 * @param contextResponses the contextResponses to set
	 */
	public void setContextResponses(List<ContextResponses> contextResponses) {
		this.contextResponses = contextResponses;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ContextBrokerResponse [contextResponses=" + contextResponses + "]";
	}

}
