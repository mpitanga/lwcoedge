package br.edu.ufrj.lwcoedge.core.interfaces;

import br.edu.ufrj.lwcoedge.core.model.Request;

public interface IRest {

	public void sendRequest(Request request, String... args) throws Exception;
}
