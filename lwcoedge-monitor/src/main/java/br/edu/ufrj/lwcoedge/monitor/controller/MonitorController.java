package br.edu.ufrj.lwcoedge.monitor.controller;

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

import br.edu.ufrj.lwcoedge.core.interfaces.IMonitor;
import br.edu.ufrj.lwcoedge.core.model.Metrics;
import br.edu.ufrj.lwcoedge.core.model.ResourcesAvailable;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;

@RestController
@RequestMapping("/monitor")
public class MonitorController implements ApplicationRunner {

	@Autowired
	IMonitor service;
	
	@PostMapping("/vn/metrics")
	public Metrics getVirtualNodeMetrics(@RequestBody VirtualNode vn, HttpServletRequest httpRequest) {
		try {
			return service.getVirtualNodeMetrics(vn);
		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, 
					"Error fetching virtual node metrics. Cause: "+e.getMessage());
		}
	}
	
	@GetMapping("/node/resources")
	public ResourcesAvailable getNodeResources(HttpServletRequest httpRequest) {
		try {
			final String RequestID = httpRequest.getHeader("RequestID");
			final String startDateTime = httpRequest.getHeader("StartDateTime");
			final String experimentID = httpRequest.getHeader("ExperimentID");
			long requestSize = httpRequest.getContentLengthLong();
			if (requestSize < 0) {
				requestSize = 0;
			}
			final String requestSizeStr = String.valueOf(requestSize);
			return service.getNodeResources(RequestID, startDateTime, experimentID, requestSizeStr);
		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Error fetching node resources. Cause: "+e.getMessage());
		}
	}

	@GetMapping("/percentage/memavailable/{value}")
	public void setPercentualMinMemAvailable(@PathVariable double value) {
		service.setPercentualMinMemAvailable(value);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
		service.appConfig(args);
	}
}
