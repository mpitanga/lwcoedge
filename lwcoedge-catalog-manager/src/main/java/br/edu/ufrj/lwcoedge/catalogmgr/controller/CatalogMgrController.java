package br.edu.ufrj.lwcoedge.catalogmgr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ufrj.lwcoedge.catalogmgr.service.CatalogMgrService;
import br.edu.ufrj.lwcoedge.core.model.Datatype;
import br.edu.ufrj.lwcoedge.core.model.Descriptor;

@RestController
@RequestMapping("/catalog")
public class CatalogMgrController {

	@Autowired
	CatalogMgrService service;
	
	@PostMapping("/descriptor")
	public Descriptor getDescriptor(@RequestBody Datatype datatype) {
		return service.getDescriptor(datatype.getId());
	}
	
	@GetMapping("/all")
	public Object getAll() {
		return service.getAll();
	}

}
