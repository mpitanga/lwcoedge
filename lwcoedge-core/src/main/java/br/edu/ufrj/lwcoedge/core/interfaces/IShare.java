package br.edu.ufrj.lwcoedge.core.interfaces;

import java.util.ArrayList;

import br.edu.ufrj.lwcoedge.core.model.Data;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;

public interface IShare {

	public void shareData(VirtualNode virtualNode, String element, ArrayList<Data> data, String... args) throws Exception;
}
