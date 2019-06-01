package br.edu.ufrj.lwcoedge.monitor.service;

import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.boot.ApplicationArguments;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sun.management.OperatingSystemMXBean;

import br.edu.ufrj.lwcoedge.core.cache.Cache;
import br.edu.ufrj.lwcoedge.core.interfaces.IMonitor;
import br.edu.ufrj.lwcoedge.core.model.Metrics;
import br.edu.ufrj.lwcoedge.core.model.ResourcesAvailable;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;
import br.edu.ufrj.lwcoedge.core.service.AbstractService;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.monitor.model.ActuatorMetrics;
import br.edu.ufrj.lwcoedge.monitor.model.Measurements;

@Service
public class MonitorService extends AbstractService implements IMonitor {

	// This constant defines the amount of data types per edge node
	private final int MAX_ELEMENTS2 = 2;
	private final int TIMETOLIVE = 600; // 10 min
	private final int TIMEINTERVAL = 600; // 10 min
	private final int ONEDAY = 86400;
		
	// Key - Value
    private Cache<String, Measurements> cache = new Cache<String, Measurements>(TIMETOLIVE, TIMEINTERVAL, MAX_ELEMENTS2);
    private Cache<String, Long> cache2 = new Cache<String, Long>(ONEDAY, ONEDAY, MAX_ELEMENTS2);

    private double PERC_MIN_MEMAVAILABLE = 5.0;
    
    private String vnInstanceCacheUrl;
    
    @Override
    public void appConfig(ApplicationArguments args) throws Exception {
		this.getLogger().info("LW-CoEdge loading application settings...\n");
		if (args != null && !args.getOptionNames().isEmpty()) {
			if (args.getOptionValues("PercMinMemAvailable") != null && !args.getOptionValues("PercMinMemAvailable").isEmpty()) {
				PERC_MIN_MEMAVAILABLE = Double.parseDouble(args.getOptionValues("PercMinMemAvailable").get(0));
			}
			
			this.loadComponentsPort(args);
			this.vnInstanceCacheUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_vn_instancecache(), "/vninstancecache/search");
			this.getLogger().info("VNInstance cache url = {}", this.vnInstanceCacheUrl);

			this.getLogger().info("% Min of available memory : {}", Double.toString(PERC_MIN_MEMAVAILABLE));

		} else {
			this.getLogger().error("No application settings founded!");
			System.exit(-1);
		}		
		this.getLogger().info("");
		this.getLogger().info("LW-CoEdge application settings loaded.\n");
		
    }

	private ActuatorMetrics getActuatorMetrics(String host, Integer port, String metric) throws Exception {
		StringBuilder url = new StringBuilder();
		url.append("http://").append(host).append(":").append(port).append("/actuator/metrics/").append(metric);
//		this.getLogger().info("URL : {}", url.toString());

		ResponseEntity<ActuatorMetrics> am = 
				Util.sendRequest(url.toString(), Util.getDefaultHeaders(), HttpMethod.GET, null, ActuatorMetrics.class);
		
		return am.getBody();		
	}
		
	private final BigDecimal ONEKB = new BigDecimal(1024);

	private Metrics getMetrics(String hostname, Integer port, String datatypeId) throws Exception {
		Metrics m = new Metrics();
		try {			
			ActuatorMetrics am;
			Measurements systemCpuCount = cache.get("system.cpu.count");
			if (systemCpuCount == null) {
				am = getActuatorMetrics(hostname, port, "system.cpu.count");
				systemCpuCount = am.getMeasurements().get(0);
				cache.put("system.cpu.count", systemCpuCount);
			}
			
			am = getActuatorMetrics(hostname, port, "jvm.memory.committed");
			Measurements jvmMemoryCommitted = am.getMeasurements().get(0);
			
			am = getActuatorMetrics(hostname, port, "jvm.memory.used");
			Measurements jvmMemoryUsed = am.getMeasurements().get(0);
			
			am = getActuatorMetrics(hostname, port, "jvm.threads.live");
			Measurements threadsBusy = am.getMeasurements().get(0);
			
			am = getActuatorMetrics(hostname, port, "jvm.threads.peak");
			Measurements threadsPeak  = am.getMeasurements().get(0);
		
			BigDecimal memKB = jvmMemoryCommitted.getValue().divide(ONEKB, 2, RoundingMode.HALF_UP);
			BigDecimal memUsedKB = jvmMemoryUsed.getValue().divide(ONEKB, 2, RoundingMode.HALF_UP);
			
			m.setMem(memKB.intValue());
			m.setMemUsed(memUsedKB.intValue());
			m.setMemFree(memKB.subtract(memUsedKB).intValue());
			m.setUnit("K");
			m.setNumberOfProcessors(systemCpuCount.getValue().intValue());
			m.setThreadsTotal(threadsPeak.getValue().intValue());
			m.setThreads(threadsBusy.getValue().intValue());
			m.setThreadPeek(threadsPeak.getValue().intValue());
			
			double memReduce = Util.calcPercentage(memKB.longValue(), memUsedKB.longValue(), 1);

			m.setResourceBusy((memReduce < PERC_MIN_MEMAVAILABLE ? true : false));
			
			this.getLogger().debug("Datatype : {} - VirtualNodeMetrics [Mem: {} MemUsed: {}] - Is Busy? {}  % available = {}",
					datatypeId, m.getMem(), m.getMemUsed(), m.isResourceBusy(), memReduce);

		} catch (Exception e) {
			this.getLogger().error( "[ERROR] Generationg Metrics object. Error= {}", e.getMessage() );
		}
		return m;

	}
	
	@Override
	public Metrics getVirtualNodeMetrics(VirtualNode vn, String... args) throws Exception {
		return getMetrics(vn.getHostName(), vn.getPort(), vn.getDatatype().getDescriptorId());
	}
	
	@Override
	public ResourcesAvailable getNodeResources(String... args) throws Exception {		
		OperatingSystemMXBean mxbean = 
	    		(OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		
		Long totalPhysicalMemorySize = cache2.get("TotalPhysicalMemorySize");
		if (totalPhysicalMemorySize == null) {
			totalPhysicalMemorySize = mxbean.getTotalPhysicalMemorySize();
			BigDecimal memKB = BigDecimal.valueOf(totalPhysicalMemorySize).divide(ONEKB, 2, RoundingMode.HALF_UP);
			cache2.put("TotalPhysicalMemorySize", memKB.longValue());
		}
		Long availableProcessors = cache2.get("AvailableProcessors");
		if (availableProcessors == null) {
			availableProcessors = Integer.toUnsignedLong(mxbean.getAvailableProcessors());
			cache2.put("AvailableProcessors", availableProcessors);
		}

		long freePhysicalMemorySize = mxbean.getFreePhysicalMemorySize();
		BigDecimal freememKB = BigDecimal.valueOf(freePhysicalMemorySize).divide(ONEKB, 2, RoundingMode.HALF_UP);
		freePhysicalMemorySize = freememKB.longValue();
		
		ResourcesAvailable res = new ResourcesAvailable(
				totalPhysicalMemorySize, 
				freePhysicalMemorySize, 
				availableProcessors,
				"K");
		return 	res;
	}

	@Override
	public void setPercentualMinMemAvailable(double value) {
		this.PERC_MIN_MEMAVAILABLE = value;
	}

}
