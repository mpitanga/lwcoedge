package br.edu.ufrj.lwcoedge.core.interfaces;

import br.edu.ufrj.lwcoedge.core.model.Datatype;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;

public interface ISearch {
	/**
	 * 
	 * @param datatype
	 * @return
	 */
	public VirtualNode getSearch(Datatype datatype, String... args);
}
