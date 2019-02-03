package br.edu.ufrj.lwcoedge.catalogmgr.service;

import java.io.Serializable;
import java.util.ArrayList;

import br.edu.ufrj.lwcoedge.core.model.Descriptor;

public class Repository implements Serializable {

	private static final long serialVersionUID = -5789902966342745872L;

	private ArrayList<Descriptor> descriptors;
	
	public Repository() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<Descriptor> getDescriptors() {
		return descriptors;
	}

	public void setDescriptors(ArrayList<Descriptor> descriptors) {
		this.descriptors = descriptors;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Repository [descriptors=");
		builder.append(descriptors);
		builder.append("]");
		return builder.toString();
	}
	
}
