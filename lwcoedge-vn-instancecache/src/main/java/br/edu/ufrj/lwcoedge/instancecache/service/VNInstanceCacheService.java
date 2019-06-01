package br.edu.ufrj.lwcoedge.instancecache.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

import br.edu.ufrj.lwcoedge.core.cache.Cache;
import br.edu.ufrj.lwcoedge.core.interfaces.IAppConfig;
import br.edu.ufrj.lwcoedge.core.interfaces.IList;
import br.edu.ufrj.lwcoedge.core.interfaces.IPersistence;
import br.edu.ufrj.lwcoedge.core.interfaces.ISearch;
import br.edu.ufrj.lwcoedge.core.model.Datatype;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;
import br.edu.ufrj.lwcoedge.core.model.VirtualNodeInstances;
import br.edu.ufrj.lwcoedge.core.service.AbstractService;
import br.edu.ufrj.lwcoedge.core.service.SendMetricService;

@Service
@ComponentScan("br.edu.ufrj.lwcoedge.core")
public class VNInstanceCacheService extends AbstractService implements IAppConfig, ISearch, IPersistence, IList {
	
	@Autowired
	SendMetricService metricService;
	
	// This constant defines the amount of data types per edge node
	private final int MAX_ELEMENTS = 100;
	private final int TIMETOLIVE = 0; // no expires
	private final int TIMEINTERVAL = 0; // no expires

	// Key - Value
    private Cache<String, VirtualNode> cache = new Cache<String, VirtualNode>(TIMETOLIVE, TIMEINTERVAL, MAX_ELEMENTS);

    private String managerApiUrl;
    
    @Override
    public void appConfig(ApplicationArguments args) throws Exception {    	
		this.getLogger().info("LW-CoEdge loading application settings...\n");
		if (args != null && !args.getOptionNames().isEmpty()) {
			try {
				this.loadComponentsPort(args);
				this.managerApiUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_manager_api(), "/lwcoedgemgr/metrics/put");
				this.getLogger().info("Manager API url = {}", this.managerApiUrl );
			} catch (Exception e) {
				this.getLogger().error(e.getMessage());
				System.exit(-1);
			}
		} else {
			this.getLogger().error("No application settings founded!");
			System.exit(-1);
		}    	
		this.getLogger().info("");
		this.getLogger().info("LW-CoEdge application settings loaded.\n");
    }
 
    @Override
    public void register(VirtualNode value, String... args) {
    	this.cache.put(value.getDatatype().getDescriptorId(), value);
    }

    @Override
    public void remove(String key, String... args) {
    	this.cache.remove(key);
    }   
 
    @Override
	public VirtualNode getSearch(Datatype datatype, String... args) {
   		return this.cache.get(datatype.getId());
	}

    @Override
	public void empty(String... args) {
		this.cache.clearAll();
	}
	
    @Override
	public VirtualNodeInstances getListInstances(String... args) {
    	VirtualNodeInstances vni = new VirtualNodeInstances();
    	vni.setVirtualNodeInstances(this.cache.getAll());
		return vni;
	}

}
