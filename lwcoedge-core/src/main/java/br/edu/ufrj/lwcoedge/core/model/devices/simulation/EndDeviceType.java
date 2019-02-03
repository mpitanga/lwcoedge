package br.edu.ufrj.lwcoedge.core.model.devices.simulation;

import java.io.Serializable;

public enum EndDeviceType implements Serializable {
	SENSOR (0, "sensor") {
		public EndDeviceType getType() {
			return SENSOR;
		};
	},
	ACTUATOR (1, "actuator") {
		public EndDeviceType getType() {
			return ACTUATOR;
		};
	};

	public abstract EndDeviceType getType();
	
	private int ord;
	private String type;
	
	EndDeviceType(int ord, String type) {
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
