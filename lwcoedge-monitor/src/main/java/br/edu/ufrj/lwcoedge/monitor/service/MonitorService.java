package br.edu.ufrj.lwcoedge.monitor.service;

import java.lang.annotation.Native;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.MathContext;
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
	@Native private static int MAX_ELEMENTS = 50;
	@Native private static int TIMETOLIVE = 600; // 10 min
	@Native private static int TIMEINTERVAL = 600; // 10 min
	@Native private static int ONEDAY = 86400;
		
	// Key - Value
    private Cache<String, Measurements> cache = new Cache<String, Measurements>(TIMETOLIVE, TIMEINTERVAL, MAX_ELEMENTS);
    private Cache<String, Long> cache2 = new Cache<String, Long>(ONEDAY, ONEDAY, MAX_ELEMENTS);

    private double PERC_MIN_MEMAVAILABLE = 5.0;
    
    @Override
    public void appConfig(ApplicationArguments args) throws Exception {
		if (args != null && !args.getOptionNames().isEmpty()) {
			if (args.getOptionValues("PercMinMemAvailable") != null && !args.getOptionValues("PercMinMemAvailable").isEmpty()) {
				PERC_MIN_MEMAVAILABLE = Double.parseDouble(args.getOptionValues("PercMinMemAvailable").get(0));
			}
		}
		
		this.getLogger().info( Util.msg("% Min of available memory : ", Double.toString(PERC_MIN_MEMAVAILABLE)));
    }

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
			Measurements systemCpuCount = cache.get("system.cpu.count");
			if (systemCpuCount == null) {
				am = getActuatorMetrics(vn.getHostName(), vn.getPort(), "system.cpu.count");
				systemCpuCount = am.getMeasurements().get(0);
				cache.put("system.cpu.count", systemCpuCount);
			}
			
/*			Measurements jvmMemoryCommitted = cache.get("jvm.memory.committed");
			if (jvmMemoryCommitted == null) {
				am = getActuatorMetrics(vn.getHostName(), vn.getPort(), "jvm.memory.committed");
				jvmMemoryCommitted = am.getMeasurements().get(0);
				cache.put("jvm.memory.committed", jvmMemoryCommitted);
			}
*/
			am = getActuatorMetrics(vn.getHostName(), vn.getPort(), "jvm.memory.committed");
			Measurements jvmMemoryCommitted = am.getMeasurements().get(0);
			
			am = getActuatorMetrics(vn.getHostName(), vn.getPort(), "jvm.memory.used");
			Measurements jvmMemoryUsed = am.getMeasurements().get(0);
			
			am = getActuatorMetrics(vn.getHostName(), vn.getPort(), "jvm.threads.live");
			Measurements threadsBusy = am.getMeasurements().get(0);
			
			am = getActuatorMetrics(vn.getHostName(), vn.getPort(), "jvm.threads.peak");
			Measurements threadsPeak  = am.getMeasurements().get(0);
		
			m.setMem(jvmMemoryCommitted.getValue().intValue());
			m.setMemUsed(jvmMemoryUsed.getValue().intValue());
			m.setNumberOfProcessors(systemCpuCount.getValue().intValue());
			m.setThreadsTotal(threadsPeak.getValue().intValue());
			m.setThreads(threadsBusy.getValue().intValue());
			m.setThreadPeek(threadsPeak.getValue().intValue());
			
			double memReduce = memoryReduce(m);

			m.setResourceBusy((memReduce < PERC_MIN_MEMAVAILABLE ? true : false));
			
			this.getLogger().info( Util.msg("VirtualNodeMetrics : ", m.toString(), 
					" Is Busy? ", Boolean.toString(m.isResourceBusy()),
					" % available = ", Double.toString(memReduce))
					);
			
		} catch (Exception e) {
			this.getLogger().info( Util.msg("[ERROR] Generationg Metrics object. Error=", e.getMessage()) );
		}
		return m;
	}

	//one hundred
	@Native private static BigDecimal ONE_HUNDRED = new BigDecimal(100);
	private double memoryReduce(Metrics m) {
		BigDecimal r1 = new BigDecimal ( m.getMemUsed() ).divide(new BigDecimal(m.getMem()), 2, RoundingMode.HALF_UP);
		BigDecimal r2 = new BigDecimal(1).subtract(r1).round(new MathContext(2, RoundingMode.HALF_UP));
		BigDecimal memReduce = r2.multiply(ONE_HUNDRED).setScale(1, RoundingMode.HALF_UP);
		return memReduce.doubleValue();
	}

/*
	public static void main(String[] args) {
		BigDecimal n1 = new BigDecimal ("1.11407208E8");
		BigDecimal n2 = new BigDecimal ("1.33955584E8");
		System.out.println(n1.longValue());
		System.out.println(n2.longValue());
		
			Integer total = 127598592;
			Integer used  = 112366648;
			BigDecimal r1 = new BigDecimal(used).divide( new BigDecimal(total), 2, RoundingMode.HALF_UP );
			BigDecimal r2 = new BigDecimal(1).subtract(r1).round(new MathContext(2, RoundingMode.HALF_UP));
			BigDecimal memReduce = r2.multiply(new BigDecimal(100)).setScale(1, RoundingMode.HALF_UP);
			System.out.println(used);
			System.out.println(total);
			System.out.println(total-used);
			System.out.println(r1);
			System.out.println(r2);
			System.out.println(memReduce);
			System.out.println( (memReduce.doubleValue()<=12) ? true : false );
	}
*/	
	
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

	@Override
	public void setPercentualMinMemAvailable(double value) {
		this.PERC_MIN_MEMAVAILABLE = value;
	}

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
