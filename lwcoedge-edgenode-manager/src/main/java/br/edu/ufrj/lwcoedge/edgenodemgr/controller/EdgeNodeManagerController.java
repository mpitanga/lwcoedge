package br.edu.ufrj.lwcoedge.edgenodemgr.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import br.edu.ufrj.lwcoedge.core.model.Datatype;
import br.edu.ufrj.lwcoedge.core.model.Descriptor;
import br.edu.ufrj.lwcoedge.core.model.EdgeNode;
import br.edu.ufrj.lwcoedge.core.model.Resources;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.edgenodemgr.service.EdgeNodeManagerService;

@RestController
@RequestMapping("/edgenodemanager")
public class EdgeNodeManagerController implements ApplicationRunner {

	@Autowired
	EdgeNodeManagerService service;
	
	@PostMapping("/hasResource")
	public boolean hasResource(@RequestBody Datatype datatype, HttpServletRequest httpRequest) throws Exception {
		final String requestSize = String.valueOf(httpRequest.getContentLengthLong());
		return service.hasResource(datatype, requestSize);
	}
	
	@PostMapping("/hasConnectedDevices")
	public boolean hasConnectedDevices(@RequestBody Descriptor datatype, HttpServletRequest httpRequest) {
		final String RequestID = httpRequest.getHeader("RequestID");
		final String startDateTime = httpRequest.getHeader("StartDateTime");
		final String experimentID = httpRequest.getHeader("ExperimentID");
		final String requestSize = String.valueOf(httpRequest.getContentLengthLong());
		return service.hasConnectedDevices(datatype,  RequestID, startDateTime, experimentID, requestSize);
	}
	
	@PostMapping("/containerDeploy")
	public VirtualNode containerDeploy(@RequestBody Datatype datatype, HttpServletRequest httpRequest) {
		try {
			final String RequestID = httpRequest.getHeader("RequestID");
			final String startDateTime = httpRequest.getHeader("StartDateTime");
			final String experimentID = httpRequest.getHeader("ExperimentID");
			return service.containerDeploy(datatype, RequestID, startDateTime, experimentID);
		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, 
					Util.msg("The container was not deployed. Cause: ",e.getMessage()));
		}
	}
	
	@PostMapping("/scaleUp")
	public boolean scaleUp(VirtualNode vn, HttpServletRequest httpRequest) {
		return service.scaleUp(vn);
	}
	
	@PostMapping("/scaleDown")
	public boolean scaleDown(VirtualNode vn, HttpServletRequest httpRequest) {
		return service.scaleDown(vn);
	}

	@PostMapping("/containerStop")
	public void containerStop(VirtualNode vn, HttpServletRequest httpRequest) {
		service.containerStop(vn);
	}

	@GetMapping("/edgenode/config")
	public EdgeNode getEdgeNode() {
		return service.getEdgeNode();
	}
	
	@GetMapping("/datatype/neededresources/all")
	public Object getMinimalResources() {
		return service.getMinimalResources();
	}
	
	@GetMapping("/datatype/neededresources/{type}")
	public Resources getNeededResources(@PathVariable String type) {
		return service.getNeededResources(type);
	}
	
	@GetMapping("/config/reload")
	public void reload() {
		try {
			service.loadEdgenodeConfig();
		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, 
					Util.msg("The new edge node configuration not loaded. Cause: ",e.getMessage()));
		}
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		service.appConfig(args);
	}

}
