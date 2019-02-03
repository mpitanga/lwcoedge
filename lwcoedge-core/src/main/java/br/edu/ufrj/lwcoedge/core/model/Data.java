package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class Data implements Serializable {
	private static final long serialVersionUID = 3762020848787080243L;
	
	private Object value;
	private String acquisitiondatetime;
	
	@JsonIgnore
	private LocalDateTime internalAcquisitiondatetime;
	
	public Data() {}
	/**
	 * @param value
	 * @param acquisitiondatetime
	 */
	public Data(Object value, String acquisitiondatetime) {
		super();
		this.value = value;
		this.acquisitiondatetime = acquisitiondatetime;
		this.setAcquisitiondatetime(acquisitiondatetime);
	}
	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}
	/**
	 * @return the acquisitiondatetime
	 */
	public String getAcquisitiondatetime() {
		return acquisitiondatetime;
	}
	
	@JsonIgnore
	public LocalDateTime getInternalAcquisitiondatetime() {
		return this.internalAcquisitiondatetime;
	}
	/**
	 * @param acquisitiondatetime the acquisitiondatetime to set
	 * @throws ParseException 
	 */
	public void setAcquisitiondatetime(String acquisitiondatetime) {
		this.acquisitiondatetime = acquisitiondatetime;
		this.internalAcquisitiondatetime = 
				LocalDateTime.parse(acquisitiondatetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((acquisitiondatetime == null) ? 0 : acquisitiondatetime.hashCode());
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
		Data other = (Data) obj;
		if (acquisitiondatetime == null) {
			if (other.acquisitiondatetime != null)
				return false;
		} else if (!acquisitiondatetime.equals(other.acquisitiondatetime))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}
}
