package br.edu.ufrj.lwcoedge.p2pdatasharing.controller;

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

import br.edu.ufrj.lwcoedge.core.interfaces.IShare;
import br.edu.ufrj.lwcoedge.core.model.DataSharing;
import br.edu.ufrj.lwcoedge.core.util.Util;

@RestController
@RequestMapping("/p2pdatasharing")
public class P2PDataSharingController implements ApplicationRunner {
	
	@Autowired
	IShare service;
	
	@GetMapping("/dt")
	public LocalDateTime getDT() {
		return LocalDateTime.now();
	}
	
	@GetMapping("/enable/{enable}")
	public void setEnable(@PathVariable boolean enable) {
		service.setDataSharing(enable);
	}
	
	@PostMapping("/sharedata")
	public void shareData(@RequestBody DataSharing ds, HttpServletRequest httpRequest) {
		try {
			final String requestID = httpRequest.getHeader("RequestID");
			final String startDateTime = httpRequest.getHeader("StartDateTime");
			final String experimentID = httpRequest.getHeader("ExperimentID");
			final String requestSize = String.valueOf(httpRequest.getContentLengthLong());

			service.shareData(ds.getVirtualNode(), ds.getDs().getElement(), ds.getDs().getData(), requestID, startDateTime, experimentID, requestSize);
			
		} catch (Exception e) {
			throw 
			new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, 
				Util.msg("The data sharing to the neighboring virtual nodes failed. Cause: ",e.getMessage())
			);
		}
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		service.appConfig(args);
	}
}
