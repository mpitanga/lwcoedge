package br.com.context.broker.elements;

import java.io.Serializable;

public class ContextResponses implements Serializable {

	private static final long serialVersionUID = -4556837933727111809L;

	private StatusCode statusCode;
    private ContextElement contextElement;
    
	public ContextResponses() {
	}

	/**
	 * @return the statusCode
	 */
	public StatusCode getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(StatusCode statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the contextElement
	 */
	public ContextElement getContextElement() {
		return contextElement;
	}

	/**
	 * @param contextElement the contextElement to set
	 */
	public void setContextElement(ContextElement contextElement) {
		this.contextElement = contextElement;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ContextResponses [statusCode=" + statusCode + ", contextElement=" + contextElement + "]";
	}

}
