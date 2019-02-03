package br.edu.ufrj.lwcoedge.core.interfaces;

import java.util.ArrayList;

import br.edu.ufrj.lwcoedge.core.model.Data;

public interface IReceive {
	public void setData(String element, ArrayList<Data> data, String... args);
}
