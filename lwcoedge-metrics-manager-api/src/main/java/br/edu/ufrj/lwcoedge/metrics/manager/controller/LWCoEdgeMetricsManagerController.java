package br.edu.ufrj.lwcoedge.metrics.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricCollected;
import br.edu.ufrj.lwcoedge.metrics.manager.service.ExperimentService;

@RestController
@RequestMapping("/lwcoedgemgr")
public class LWCoEdgeMetricsManagerController {

	@Autowired
	ExperimentService experimentService;
	
	@GetMapping("/metrics/experiment/clear")
	public void clear() {
		try {
			experimentService.clearAll();
		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping("/metrics/enable")
	public String enable() {
		try {
			experimentService.setActive(true);
			return "Metrics collect -> status ["+experimentService.isActive()+"]";
		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping("/metrics/disable")
	public String disable() {
		try {
			experimentService.setActive(false);
			return "Metrics collect -> status ["+experimentService.isActive()+"]";
		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping("/metrics/results")
	public Object getResults() {
		try {
			return experimentService.getAll();
		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping("/metrics/results/keys")
	public Object getKeys() {
		try {
			return experimentService.getKeys();
		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@GetMapping("/metrics/results/keys/{metric}")
	public Object getResults(@PathVariable String metric) {
		try {
			return experimentService.getAll(metric);
		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	@PostMapping("/metrics/put")
	public void put(@RequestBody MetricCollected mc) {
		try {
			experimentService.put(mc);
		} catch (Exception e) {
			throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
