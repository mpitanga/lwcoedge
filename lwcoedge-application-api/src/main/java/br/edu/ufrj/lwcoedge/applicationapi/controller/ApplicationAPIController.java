package br.edu.ufrj.lwcoedge.applicationapi.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import br.edu.ufrj.lwcoedge.core.interfaces.IRest;
import br.edu.ufrj.lwcoedge.core.model.Request;

@RestController
@RequestMapping("/lwcoedge")
public class ApplicationAPIController implements ApplicationRunner {

	@Autowired
	IRest service;
	
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
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "[ERROR] "+e.getMessage());
		}
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		service.appConfig(args);
	}
}
