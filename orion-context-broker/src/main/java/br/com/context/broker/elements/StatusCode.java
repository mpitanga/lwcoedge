package br.com.context.broker.elements;

import java.io.Serializable;

public class StatusCode implements Serializable {

	private static final long serialVersionUID = 3059009873660285477L;
	
	private String reasonPhrase;
    private String code;
	/**
	 * @return the reasonPhrase
	 */
	public String getReasonPhrase() {
		return reasonPhrase;
	}
	/**
	 * @param reasonPhrase the reasonPhrase to set
	 */
	public void setReasonPhrase(String reasonPhrase) {
		this.reasonPhrase = reasonPhrase;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StatusCode [reasonPhrase=" + reasonPhrase + ", code=" + code + "]";
	}
    
}
