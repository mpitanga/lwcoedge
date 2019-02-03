package br.edu.ufrj.lwcoedge.instancecache.service;

import java.lang.annotation.Native;
import java.util.LinkedHashMap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import br.edu.ufrj.lwcoedge.core.cache.Cache;
import br.edu.ufrj.lwcoedge.core.interfaces.IList;
import br.edu.ufrj.lwcoedge.core.interfaces.IPersistence;
import br.edu.ufrj.lwcoedge.core.interfaces.ISearch;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricIdentification;
import br.edu.ufrj.lwcoedge.core.model.Datatype;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;
import br.edu.ufrj.lwcoedge.core.model.VirtualNodeInstances;
import br.edu.ufrj.lwcoedge.core.service.AbstractService;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.core.util.UtilMetric;

@Service
public class VNInstanceCacheService extends AbstractService implements ApplicationRunner, ISearch, IPersistence, IList {
	
	// This constant defines the amount of data types per edge node
	@Native private static int MAX_ELEMENTS = 1000;
	@Native private static int TIMETOLIVE = 0; // no expires
	@Native private static int TIMEINTERVAL = 0; // no expires

	// Key - Value
    private Cache<String, VirtualNode> cache = new Cache<String, VirtualNode>(TIMETOLIVE, TIMEINTERVAL, MAX_ELEMENTS);

    private String ManagerApiUrl;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
		if (args != null && !args.getOptionNames().isEmpty()) {
			try {
				this.loadComponentsPort(args);
				this.ManagerApiUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_manager_api(), "/lwcoedgemgr/metrics/put");
				this.getLogger().info( Util.msg("Manager API url = ", this.ManagerApiUrl) );
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		} else {
			this.getLogger().info("No application settings founded!");
			System.exit(-1);
		}    	
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
		headers.put("ExperimentID", args[2]);
		headers.put("RequestSize", args[3]);
    	return headers;
    }

	private void registerMetric(Datatype datatype, String... args) {
		new Thread(()-> {
			LinkedHashMap<String, String> headers = getHeaders(args);
			boolean sendMetricEnable = (args.length>0 && !headers.get("ExperimentID").equals("R"));
			//args -> RequestID, startDateTime, experimentID, requestSize; total = 4 
			if (sendMetricEnable) {
				MetricIdentification id = new MetricIdentification(headers.get("ExperimentID"), "DT_REQ_VNCACHE", null, datatype.getId());
				try {
					Long valueOf = Long.valueOf(headers.get("RequestSize"));
					UtilMetric.sendMetricSummaryValue(this.ManagerApiUrl, id.getKey(), id.toString(), valueOf);
				} catch (Exception e) {
					String msg = Util.msg("[ERROR] ","Error submitting the metric [", id.toString(), "] to the registry.", e.getMessage());
					this.getLogger().info(msg);
				}
			}		
		}).start();
	}

}
