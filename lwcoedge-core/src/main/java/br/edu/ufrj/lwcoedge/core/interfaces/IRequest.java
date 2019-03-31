package br.edu.ufrj.lwcoedge.core.interfaces;

import br.edu.ufrj.lwcoedge.core.model.Request;

public interface IRequest extends IAppConfig {
	/**
	 * The method in charge of handling the application requests.
	 * @param request The application request.
	 * @throws Exception The message error.
	 */
	public void handleRequest(Request request, String... args) throws Exception;
}
