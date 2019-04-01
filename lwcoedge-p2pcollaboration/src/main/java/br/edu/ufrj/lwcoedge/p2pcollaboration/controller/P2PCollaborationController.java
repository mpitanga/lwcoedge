package br.edu.ufrj.lwcoedge.p2pcollaboration.controller;

import java.time.LocalDateTime;

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

import br.edu.ufrj.lwcoedge.core.interfaces.IP2Prov;
import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;
import br.edu.ufrj.lwcoedge.core.util.Util;

@RestController
@RequestMapping("/p2pcollaboration")
public class P2PCollaborationController implements ApplicationRunner{

	@Autowired
	IP2Prov service;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		service.appConfig(args);
	}

	@PostMapping("/sendToNeighborNode")
	public void sendToNeighborNode(@RequestBody Request request, HttpServletRequest httpRequest) {
		try {
			final String RequestID = httpRequest.getHeader("RequestID");
			final String startDateTime = httpRequest.getHeader("StartDateTime");
			final String experimentID = httpRequest.getHeader("ExperimentID");

			// it is created in the "registerToCollaboration" function from the ResourceProvisioner
			final String startCommDateTime = httpRequest.getHeader("StartCommDateTime");

			//new headers
			final String startP2PDateTime = LocalDateTime.now().toString();
			final String requestSize = String.valueOf(httpRequest.getContentLengthLong());

			//if the request was again forwarded
			final String timeSpentWithP2P = 
					httpRequest.getHeader("TimeSpentWithP2P") == null ? "0" : httpRequest.getHeader("TimeSpentWithP2P");

			service.sendToNeighborNode(request, RequestID, startDateTime, experimentID, 
						startCommDateTime, startP2PDateTime, requestSize, timeSpentWithP2P);
			
		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, 
					Util.msg("The request has been not forwarded to the neighboring edge node. Cause: ",e.getMessage()));
		}
	}

	@PostMapping("/registerVNtoDataSharing")
	public void registerVNtoDataSharing(@RequestBody VirtualNode newVirtualNode, HttpServletRequest httpRequest) {
		try {
			final String requestID = httpRequest.getHeader("RequestID");
			final String startDateTime = httpRequest.getHeader("StartDateTime");
			final String experimentID = httpRequest.getHeader("ExperimentID");
			final String requestSize = String.valueOf(httpRequest.getContentLengthLong());
			service.registerVNtoDataSharing(newVirtualNode, requestID, startDateTime, experimentID, requestSize);
		} catch (Exception e) {
			throw 
			new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, 
				Util.msg("The new virtual node [",newVirtualNode.getId(),"] has been not registered to data sharing. ","Cause: ",e.getMessage())
			);
		}
	}

	@GetMapping("/enable/{enable}")
	public void setEnable(@PathVariable boolean enable) {
		service.setCollaboration(enable);
	}

	@GetMapping("/config/reload")
	public void reload() {
		try {
			service.edgeNodeConfig();
			service.neededResources();
		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, 
					Util.msg("The request has been not forwarded to the neighboring edge node. Cause: ",e.getMessage()));
		}
	}
	
}
