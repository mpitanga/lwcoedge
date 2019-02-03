package br.edu.ufrj.lwcoedge.resourceallocator.controller;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.resourceallocator.service.ResourceAllocatorService;

@RestController
@RequestMapping("/resourceallocator")
public class ResourceAllocatorController {

	@Autowired
	ResourceAllocatorService service;
	
	@PostMapping("/handlerequest")
	public void handleRequest(@RequestBody Request request, HttpServletRequest httpRequest) {
		new Thread( () -> {
			try {
				final LocalDateTime edgeNodeLocalDateTime = LocalDateTime.now();
				final String RequestID = httpRequest.getHeader("RequestID");
				final String startDateTime = httpRequest.getHeader("StartDateTime");
				final String experimentID = httpRequest.getHeader("ExperimentID");
				final String requestSize = String.valueOf(httpRequest.getContentLengthLong());
				final String commLatency = httpRequest.getHeader("CommLatency");
				// If the request comes from the P2P Collaboration, to add the communication latency
				if (commLatency != null) {
					final String startP2PDateTime = httpRequest.getHeader("StartP2PDateTime");
					final String startCommDateTime = httpRequest.getHeader("StartCommDateTime");
					service.handleRequest(request, RequestID, edgeNodeLocalDateTime.toString(), experimentID, requestSize, 
							startCommDateTime, startP2PDateTime, commLatency);
				} else {
					service.handleRequest(request, RequestID, startDateTime.toString(), experimentID, requestSize);
				}
			} catch (Exception e) {
				throw 
				new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
						Util.msg("The request was not processed.\n", "Cause: ", e.getMessage())
						);
			}
		}).start();
	}
}
