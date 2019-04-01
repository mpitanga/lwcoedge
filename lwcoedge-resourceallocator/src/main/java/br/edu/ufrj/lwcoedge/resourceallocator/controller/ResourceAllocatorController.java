package br.edu.ufrj.lwcoedge.resourceallocator.controller;

import java.time.Duration;
import java.time.LocalDateTime;

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

import br.edu.ufrj.lwcoedge.core.interfaces.IRequest;
import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.util.Util;

@RestController
@RequestMapping("/resourceallocator")
public class ResourceAllocatorController implements ApplicationRunner {

	@Autowired
	IRequest service;
	
	@PostMapping("/handlerequest")
	public void handleRequest(@RequestBody Request request, HttpServletRequest httpRequest) {
		try {
			final LocalDateTime edgeNodeLocalDateTime = LocalDateTime.now();
			final String RequestID = httpRequest.getHeader("RequestID");
			final String startDateTime = httpRequest.getHeader("StartDateTime");
			final String experimentID = httpRequest.getHeader("ExperimentID");
			final String requestSize = String.valueOf(httpRequest.getContentLengthLong());
			final String startComm = httpRequest.getHeader("StartComm");
			// If the request comes from the P2P Collaboration, to add the communication latency
			if (startComm != null) {
				final String startP2PSendRequest = httpRequest.getHeader("startP2PSendRequest");
				final String timeSpentWithP2P = httpRequest.getHeader("TimeSpentWithP2P");
				long oldCommLatency = 
						httpRequest.getHeader("commLatency") == null ? 0 : Long.parseLong(httpRequest.getHeader("commLatency"));
				
				LocalDateTime start = LocalDateTime.parse(startP2PSendRequest);
				long currentLatency = Duration.between(start, edgeNodeLocalDateTime).toMillis();
				// the test is to prevent time synchronism problems
				long commLatency = oldCommLatency + (currentLatency < 0 ? 0 : currentLatency);
				
				service.handleRequest(request, RequestID, edgeNodeLocalDateTime.toString(), experimentID, requestSize, 
						startComm, Long.toString(commLatency), timeSpentWithP2P, Long.toString(oldCommLatency));
			} else {
				service.handleRequest(request, RequestID, startDateTime, experimentID, requestSize);
			}
		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
					Util.msg("The request was not processed. Cause: ", e.getMessage())
				);
		}
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		service.appConfig(args);
	}
}
