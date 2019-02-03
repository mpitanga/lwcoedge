package br.edu.ufrj.lwcoedge.monitor.service;

import java.lang.annotation.Native;
import java.lang.management.ManagementFactory;

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
	@Native private static int MAX_ELEMENTS = 50;
	@Native private static int TIMETOLIVE = 600; // 10 min
	@Native private static int TIMEINTERVAL = 600; // 10 min
	@Native private static int ONEDAY = 86400;
	
	// Key - Value
    private Cache<String, Measurements> cache = new Cache<String, Measurements>(TIMETOLIVE, TIMEINTERVAL, MAX_ELEMENTS);
    private Cache<String, Long> cache2 = new Cache<String, Long>(ONEDAY, ONEDAY, MAX_ELEMENTS);

	private ActuatorMetrics getActuatorMetrics(String host, Integer port, String metric) throws Exception {
		StringBuilder url = new StringBuilder();
		url.append("http://").append(host).append(":").append(port).append("/actuator/metrics/").append(metric);
		this.getLogger().info( Util.msg("URL : ", url.toString()));
		
		ResponseEntity<ActuatorMetrics> am = 
				Util.sendRequest(url.toString(), Util.getDefaultHeaders(), HttpMethod.GET, null, ActuatorMetrics.class);
		
		return am.getBody();		
	}
	
	@Override
	public Metrics getVirtualNodeMetrics(VirtualNode vn, String... args) throws Exception {
		Metrics m = new Metrics();
		try {
			ActuatorMetrics am;
			Measurements jvmMemoryCommitted = cache.get("jvm.memory.committed");

			if (jvmMemoryCommitted == null) {
				am = getActuatorMetrics(vn.getHostName(), vn.getPort(), "jvm.memory.committed");
				jvmMemoryCommitted = am.getMeasurements().get(0);
				cache.put("jvm.memory.committed", jvmMemoryCommitted);
			}
			Measurements systemCpuCount = cache.get("system.cpu.count");
			if (systemCpuCount == null) {
				am = getActuatorMetrics(vn.getHostName(), vn.getPort(), "system.cpu.count");
				systemCpuCount = am.getMeasurements().get(0);
				cache.put("system.cpu.count", systemCpuCount);
			}
			am = getActuatorMetrics(vn.getHostName(), vn.getPort(), "jvm.memory.used");
			Measurements jvmMemoryUsed = am.getMeasurements().get(0);
			
			am = getActuatorMetrics(vn.getHostName(), vn.getPort(), "jvm.threads.live");
			Measurements threadsBusy = am.getMeasurements().get(0);
			
			am = getActuatorMetrics(vn.getHostName(), vn.getPort(), "jvm.threads.peak");
			Measurements threadsPeak  = am.getMeasurements().get(0);
		
			m.setMem(jvmMemoryCommitted.getValue().intValue());
			m.setMemFree(jvmMemoryCommitted.getValue().intValue()-jvmMemoryUsed.getValue().intValue());
			m.setNumberOfProcessors(systemCpuCount.getValue().intValue());
			m.setThreadsTotal(threadsPeak.getValue().intValue());
			m.setThreads(threadsBusy.getValue().intValue());
			m.setThreadPeek(threadsPeak.getValue().intValue());
			
			float percBusy = m.getMemFree()/m.getMem() * 100;
			m.setBusy( (percBusy>=95) ? true : false );
		} catch (Exception e) {
			m.setBusy(false);
		}
		this.getLogger().info( Util.msg("VirtualNodeMetrics : ", m.toString(), " Is Busy? ", Boolean.toString(m.isResourceBusy())));
		return m;
	}

	@Override
	public ResourcesAvailable getNodeResources(String... args) throws Exception {
		OperatingSystemMXBean mxbean = 
	    		(OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		
		Long totalPhysicalMemorySize = cache2.get("TotalPhysicalMemorySize");
		if (totalPhysicalMemorySize == null) {
			totalPhysicalMemorySize = mxbean.getTotalPhysicalMemorySize();
			cache2.put("TotalPhysicalMemorySize", totalPhysicalMemorySize);
		}
		Long availableProcessors = cache2.get("AvailableProcessors");
		if (availableProcessors == null) {
			availableProcessors = Integer.toUnsignedLong(mxbean.getAvailableProcessors());
			cache2.put("AvailableProcessors", availableProcessors);
		}

		long freePhysicalMemorySize = mxbean.getFreePhysicalMemorySize();
		ResourcesAvailable res = new ResourcesAvailable(
				totalPhysicalMemorySize, 
				freePhysicalMemorySize, 
				availableProcessors);

		this.getLogger().info( Util.msg("Request size : ",args[3]," OS ResourcesAvailable : ", res.toString()));
		return 	res;
	}
/*
	@Override
	public ResourcesAvailable getNodeResources() {
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

		Object attribute;
		try {
			Long totalPhysicalMemorySize = cache2.get("TotalPhysicalMemorySize");
			if (totalPhysicalMemorySize == null) {
				attribute = mBeanServer.getAttribute(new ObjectName("java.lang","type","OperatingSystem"), "TotalPhysicalMemorySize");
				totalPhysicalMemorySize = Long.valueOf(attribute.toString());
				cache2.put("TotalPhysicalMemorySize", totalPhysicalMemorySize);
			}

			Long AvailableProcessors = cache2.get("AvailableProcessors");
			if (AvailableProcessors == null) {
				attribute = mBeanServer.getAttribute(new ObjectName("java.lang","type","OperatingSystem"), "AvailableProcessors");
				AvailableProcessors = Long.valueOf(attribute.toString());
				cache2.put("AvailableProcessors", AvailableProcessors);
			}

			attribute = mBeanServer.getAttribute(new ObjectName("java.lang","type","OperatingSystem"), "FreePhysicalMemorySize");
			Long freePhysicalMemorySize = Long.valueOf(attribute.toString());
			
			return 	new ResourcesAvailable(
					totalPhysicalMemorySize, 
					freePhysicalMemorySize, 
					AvailableProcessors);

		} catch (Exception e) {
			return 	new ResourcesAvailable(0L, 0L, 0L);
		}
	}
*/

/*
	public static void main(String[] args) {
		String json = 
		"{\"name\":\"jvm.memory.used\",\"description\":\"The amount of used memory\",\"baseUnit\":\"bytes\",\"measurements\":[{\"statistic\":\"VALUE\",\"value\":1.12739624E8}],\"availableTags\":[{\"tag\":\"area\",\"values\":[\"heap\",\"nonheap\"]},{\"tag\":\"id\",\"values\":[\"CodeHeap 'profiled nmethods'\",\"G1 Old Gen\",\"CodeHeap 'non-profiled nmethods'\",\"G1 Survivor Space\",\"Compressed Class Space\",\"Metaspace\",\"G1 Eden Space\",\"CodeHeap 'non-nmethods'\"]}]}";
		try {
			ActuatorMetrics am = Util.json2obj(json, ActuatorMetrics.class);
			Measurements m = am.getMeasurements().get(0);
			System.out.println(m.getValue().longValue());
			
			BigDecimal d = new BigDecimal("1.12739624E8");
			System.out.println(d.intValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

*/
}
