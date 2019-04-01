package br.edu.ufrj.lwcoedge.instancecache.service;

import java.lang.annotation.Native;
import java.util.LinkedHashMap;

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
import br.edu.ufrj.lwcoedge.core.util.Util;

@Service
@ComponentScan("br.edu.ufrj.lwcoedge.core")
public class VNInstanceCacheService extends AbstractService implements IAppConfig, ISearch, IPersistence, IList {
	
	@Autowired
	SendMetricService metricService;
	
	// This constant defines the amount of data types per edge node
	@Native private static int MAX_ELEMENTS = 1000;
	@Native private static int TIMETOLIVE = 0; // no expires
	@Native private static int TIMEINTERVAL = 0; // no expires

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
				this.getLogger().info( Util.msg("Manager API url = ", this.managerApiUrl) );
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		} else {
			this.getLogger().info("No application settings founded!");
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
    	registerMetric(datatype, args);
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
    
    private LinkedHashMap<String, String> getHeaders(String... args) {
    	LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
    	headers.put("RequestID", args[0]);
		headers.put("ExperimentID", args[2]);
		headers.put("RequestSize", args[3]);
    	return headers;
    }

	private void registerMetric(Datatype datatype, String... args) {
		try {
			LinkedHashMap<String, String> headers = getHeaders(args);

			boolean sendMetricEnable = false;
			try {
				sendMetricEnable = (args.length>0 && !headers.get("ExperimentID").equals("R"));
				if (sendMetricEnable) {
					Long valueOf = Long.valueOf(headers.get("RequestSize"));
					metricService.sendMetricSummaryValue(managerApiUrl, headers.get("ExperimentID"), "DT_REQ_VNCACHE", datatype.getId(), valueOf);
				}
			} catch (Exception e) {
				String msg = Util.msg("[ERROR_2] ","Error submitting the metric of request [", args[0], "] to the registry. ", e.getMessage());
				this.getLogger().info(msg);
			}

		} catch (Exception e) {
			String msg = Util.msg("[ERROR_1] ","Error submitting the metric of request [", args[0], "] to the registry. ", e.getMessage());
			this.getLogger().info(msg);
		}
	}

}
