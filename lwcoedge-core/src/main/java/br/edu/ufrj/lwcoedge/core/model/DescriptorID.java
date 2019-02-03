package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class DescriptorID implements Serializable {
	private static final long serialVersionUID = 5927119798412091208L;

	private String where;
	private String who;
	private String what;
	
	public DescriptorID() {}
	
	/**
	 * @param where
	 * @param who
	 * @param what
	 */
	public DescriptorID(String where, String who, String what) {
		super();
		this.where = where;
		this.who = who;
		this.what = what;
	}
	
	/**
	 * @return the where
	 */
	public String getWhere() {
		return where;
	}
	/**
	 * @param where the where to set
	 */
	public void setWhere(String where) {
		this.where = where;
	}
	/**
	 * @return the who
	 */
	public String getWho() {
		return who;
	}
	/**
	 * @param who the who to set
	 */
	public void setWho(String who) {
		this.who = who;
	}
	/**
	 * @return the what
	 */
	public String getWhat() {
		return what;
	}
	/**
	 * @param what the what to set
	 */
	public void setWhat(String what) {
		this.what = what;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
		result = prime * result + ((what == null) ? 0 : what.hashCode());
		result = prime * result + ((where == null) ? 0 : where.hashCode());
		result = prime * result + ((who == null) ? 0 : who.hashCode());
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
		DescriptorID other = (DescriptorID) obj;
		if (what == null) {
			if (other.what != null)
				return false;
		} else if (!what.equals(other.what))
			return false;
		if (where == null) {
			if (other.where != null)
				return false;
		} else if (!where.equals(other.where))
			return false;
		if (who == null) {
			if (other.who != null)
				return false;
		} else if (!who.equals(other.who))
			return false;
		return true;
	}
}
