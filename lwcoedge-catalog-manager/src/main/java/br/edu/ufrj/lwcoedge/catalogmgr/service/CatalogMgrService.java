package br.edu.ufrj.lwcoedge.catalogmgr.service;

import java.io.File;
import java.io.IOException;

import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ufrj.lwcoedge.core.cache.Cache;
import br.edu.ufrj.lwcoedge.core.interfaces.IAppConfig;
import br.edu.ufrj.lwcoedge.core.interfaces.ICatalog;
import br.edu.ufrj.lwcoedge.core.interfaces.IRegistry;
import br.edu.ufrj.lwcoedge.core.interfaces.IVNCat;
import br.edu.ufrj.lwcoedge.core.model.Descriptor;
import br.edu.ufrj.lwcoedge.core.service.AbstractService;
import br.edu.ufrj.lwcoedge.core.util.Util;

@Service
public class CatalogMgrService extends AbstractService implements IAppConfig, IVNCat, ICatalog, IRegistry {

	// This constant defines the amount of data types per edge node
	private final int MAX_ELEMENTS = 100;
	private final int TIMETOLIVE = 0; // no expires
	private final int TIMEINTERVAL = 0; // no expires
	
	// Key - Value
    private Cache<String, Descriptor> cache = new Cache<String, Descriptor>(TIMETOLIVE, TIMEINTERVAL, MAX_ELEMENTS);
    
	@Override
	public void appConfig(ApplicationArguments args) {
		if (args == null || args.getOptionNames().isEmpty()) {
			this.getLogger().error("No descriptors repository found!");
			System.exit(-1);
		}
		try {
			this.getLogger().info("Loading descriptors from repository to memory cache...");
			
			String fileName = args.getOptionValues("CatalogDescriptors").get(0);
			ObjectMapper objectMapper = new ObjectMapper();
			Repository repository;
			repository = objectMapper.readValue(new File(fileName), Repository.class);
			for (Descriptor descriptor : repository.getDescriptors()) {
				cache.put(descriptor.getDescriptorId(), descriptor);
				this.getLogger().info( Util.msg(descriptor.getDescriptorId(), " - ", " loaded"));			
			}
			this.getLogger().info("Load finished!");
		} catch (IOException e) {
			this.getLogger().error( Util.msg("[ERROR] ","Load descriptors error!\n",e.getMessage()));
			System.exit(-1);
		}
	
	}

	@Override
	public Descriptor getDescriptor(String id) {
		return cache.get(id);
	}

	public Object getAll() {
		return cache.getAll();
	}

}
