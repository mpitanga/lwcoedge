package br.edu.ufrj.lwcoedge.edgenodemgr.config;

import java.io.Serializable;
import java.util.ArrayList;

import br.edu.ufrj.lwcoedge.core.model.NeededResources;

public class ConfigNeededResources implements Serializable {

	private static final long serialVersionUID = -1343113577289184741L;

	private ArrayList<NeededResources> neededResources;
	
	public ConfigNeededResources() {}

	public ArrayList<NeededResources> getNeededResources() {
		return neededResources;
	}

	public void setNeededResources(ArrayList<NeededResources> neededResources) {
		this.neededResources = neededResources;
	}

	@Override
	public String toString() {
		return "ConfigNeedRes [neededResources=" + neededResources + "]";
	}
	
}
