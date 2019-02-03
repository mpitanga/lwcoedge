/**
 * 
 */
package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;

/**
 * @author mpalv
 *
 */
public enum Type implements Serializable {
	SIMPLE (0, "simple") {
		public Type getType() {
			return SIMPLE;
		};
	},
	COMPLEX (1, "complex") {
		public Type getType() {
			return COMPLEX;
		};
	},
	ACTUATION (2, "actuation") {
		public Type getType() {
			return ACTUATION;
		};
	};

	public abstract Type getType();
	
	private int ord;
	private String type;
	
	Type(int ord, String type) {
		this.ord = ord;
		this.type = type;
	}

	public String toString() {
		return this.type.toLowerCase();
	}
	
	public int getOrdinal() {
		return ord; 
	}

}
