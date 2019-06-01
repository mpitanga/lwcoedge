package br.edu.ufrj.lwcoedge.resourceprovisioner.controller;

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

import br.edu.ufrj.lwcoedge.core.interfaces.IProvisioning;
import br.edu.ufrj.lwcoedge.core.model.ResourceProvisioningParams;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;

@RestController
@RequestMapping("/resourceprovisioner")
public class ResourceProvisionerController implements ApplicationRunner {

	@Autowired
	private IProvisioning service;
	
	@PostMapping("/provisioning")
	public VirtualNode provisioning(@RequestBody ResourceProvisioningParams params, HttpServletRequest httpRequest) {
		VirtualNode vn = null;
		try {
			final String requestID = httpRequest.getHeader("RequestID");
			final String startDateTime = 
					(httpRequest.getHeader("StartDateTime") == null) 
					? LocalDateTime.now().toString() : httpRequest.getHeader("StartDateTime");
			final String experimentID = httpRequest.getHeader("ExperimentID");
			final String requestSize = String.valueOf(httpRequest.getContentLengthLong());

			vn = service.provisioning(params.getCurrentVirtualNode(), params.getRequest(), 
					requestID, startDateTime, experimentID, requestSize);

		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
						"The request was not processed. Cause: "+ e.getMessage());

		}
		return vn;
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		service.appConfig(args);
	}

}
