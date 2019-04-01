package br.edu.ufrj.lwcoedge.vn.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import br.edu.ufrj.lwcoedge.core.model.DataToShare;
import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.vn.sensing.IVNSensingService;

@RestController
@RequestMapping("/vnsensing")
public class VirtualNodeSensingController implements ApplicationRunner {
	
	@Autowired
	IVNSensingService vnService;
	
	@PostMapping("/app_request") 
	public void appRequest (@RequestBody Request request, HttpServletRequest httpRequest) throws Exception {
		try {
			final String RequestID = httpRequest.getHeader("RequestID");
			final String startDateTime = httpRequest.getHeader("StartDateTime");
			final String experimentID = httpRequest.getHeader("ExperimentID");
			final String commLatency = httpRequest.getHeader("CommLatency");
			final String requestSize = String.valueOf(httpRequest.getContentLengthLong());
			final String timeSpentWithP2P = 
					httpRequest.getHeader("TimeSpentWithP2P") == null ? Long.toString(0l) : httpRequest.getHeader("TimeSpentWithP2P");

			vnService.handleRequest(request, RequestID, startDateTime, experimentID, commLatency, requestSize, timeSpentWithP2P);
		} catch (Exception e) {
			String msg = Util.msg("[VirtualNode] Error processing the request!\n", e.getMessage());
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, msg);
		} 
	}

	@PostMapping("/neighbor/register") 
	public void neighborRegister(@RequestBody ArrayList<String> neighbors){
		vnService.neighborRegister(neighbors);
	}

	@PostMapping("/set/data")
	public void setData(@RequestBody DataToShare ds, HttpServletRequest httpRequest) {
		final String RequestID = httpRequest.getHeader("RequestID");
		vnService.setData(ds.getElement(), ds.getData(), RequestID);
	}

	@GetMapping("/isalive")
	public boolean getIsAlive() {
		if (vnService == null)
			return false;
		return vnService.isRunning();
	}
	
	@GetMapping("/id")
	public String getVNId() {
		return vnService.getVn().getId();
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		vnService.appConfig(args);
	}
}
