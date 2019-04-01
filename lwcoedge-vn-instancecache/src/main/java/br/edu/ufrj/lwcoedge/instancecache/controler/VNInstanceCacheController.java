package br.edu.ufrj.lwcoedge.instancecache.controler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufrj.lwcoedge.core.model.Datatype;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;
import br.edu.ufrj.lwcoedge.core.model.VirtualNodeInstances;
import br.edu.ufrj.lwcoedge.instancecache.service.VNInstanceCacheService;

@RestController
@RequestMapping("/vninstancecache")
public class VNInstanceCacheController implements ApplicationRunner {

	@Autowired
	VNInstanceCacheService service;
	
	public VNInstanceCacheController() {}
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		service.appConfig(args);
	}

	@PostMapping("/search")
	public VirtualNode getData(@RequestBody Datatype key, HttpServletRequest httpRequest) {
		final String RequestID = httpRequest.getHeader("RequestID");
		final String startDateTime = httpRequest.getHeader("StartDateTime");
		final String experimentID = httpRequest.getHeader("ExperimentID");
		final String requestSize = String.valueOf(httpRequest.getContentLengthLong());

		return this.service.getSearch(key, RequestID, startDateTime, experimentID, requestSize);
	}

	@GetMapping("/cache/list/instances")
	public VirtualNodeInstances getAllData(HttpServletRequest httpRequest) {
		return this.service.getListInstances();
	}

	@PostMapping("/register")
	public void register(@RequestBody VirtualNode value) {
		this.service.register(value);
	}
	
	@GetMapping("/remove/{key}")
	public void remove(@PathVariable String key) {
		this.service.remove(key);
	}

	public void empty() {
		// TODO Auto-generated method stub
	}
	
}
