package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class ConnectedDevices implements Serializable {

	private static final long serialVersionUID = -3507598354711030408L;
	
	private String value;
	
	public ConnectedDevices() {}

	public ConnectedDevices(String value) {
		super();
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return Util.obj2json(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConnectedDevices other = (ConnectedDevices) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
