package br.edu.ufrj.lwcoedge.applicationapi.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufrj.lwcoedge.applicationapi.service.ApplicationAPIService;
import br.edu.ufrj.lwcoedge.core.model.Request;

@RestController
@RequestMapping("/lwcoedge")
public class ApplicationAPIController {

	@Autowired
	ApplicationAPIService service;
	
	@PostMapping("/request/send")
	public void sendRequest(@RequestBody Request request, HttpServletRequest httpRequest) throws Exception {
		try {
			final String expID = 
					(httpRequest.getHeader("ExperimentID") == null || httpRequest.getHeader("ExperimentID").isEmpty())
						? "R" : httpRequest.getHeader("ExperimentID");
			final String var =
					(httpRequest.getHeader("ExperimentVar") == null || httpRequest.getHeader("ExperimentVar").isEmpty()) 
						? "1" : httpRequest.getHeader("ExperimentVar");
			service.sendRequest(request, expID, var);
		} catch (Exception e) {
			throw e;
		}
	}
}
