package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;
import java.util.ArrayList;

public class DataToShare implements Serializable {

	private static final long serialVersionUID = 1984449607880378567L;

	private String element;
	private ArrayList<Data> data;
	
	public DataToShare() {}

	public DataToShare(String element, ArrayList<Data> data) {
		super();
		this.element = element;
		this.data = data;
	}

	/**
	 * @return the element
	 */
	public String getElement() {
		return element;
	}

	/**
	 * @param element the element to set
	 */
	public void setElement(String element) {
		this.element = element;
	}

	/**
	 * @return the data
	 */
	public ArrayList<Data> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(ArrayList<Data> data) {
		this.data = data;
	}

	
}
