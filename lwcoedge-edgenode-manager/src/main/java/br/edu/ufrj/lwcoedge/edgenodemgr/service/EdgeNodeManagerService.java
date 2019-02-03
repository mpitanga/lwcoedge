package br.edu.ufrj.lwcoedge.edgenodemgr.service;

import java.io.File;
import java.lang.annotation.Native;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ufrj.lwcoedge.core.cache.Cache;
import br.edu.ufrj.lwcoedge.core.interfaces.IEdgeMgr;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricIdentification;
import br.edu.ufrj.lwcoedge.core.model.ConnectedDevices;
import br.edu.ufrj.lwcoedge.core.model.Datatype;
import br.edu.ufrj.lwcoedge.core.model.Descriptor;
import br.edu.ufrj.lwcoedge.core.model.EdgeNode;
import br.edu.ufrj.lwcoedge.core.model.Element;
import br.edu.ufrj.lwcoedge.core.model.NeededResources;
import br.edu.ufrj.lwcoedge.core.model.NeededResourcesCache;
import br.edu.ufrj.lwcoedge.core.model.Resources;
import br.edu.ufrj.lwcoedge.core.model.ResourcesAvailable;
import br.edu.ufrj.lwcoedge.core.model.Type;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;
import br.edu.ufrj.lwcoedge.core.service.AbstractService;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.core.util.UtilMetric;
import br.edu.ufrj.lwcoedge.edgenodemgr.config.ConfigEdgenode;
import br.edu.ufrj.lwcoedge.edgenodemgr.config.ConfigNeededResources;

@Service
public class EdgeNodeManagerService extends AbstractService implements IEdgeMgr, ApplicationRunner {

	private NeededResourcesCache neededRes = new NeededResourcesCache();
	private ConfigEdgenode configEdgenode;
	
	private String CatalogMgrUrl, ManagerApiUrl;
	
	private String fileName = null;

	// Defines a amount of ports
	@Native private static int MAX_ELEMENTS = 3;
	private Cache<String, Integer> cachePorts = new Cache<String, Integer>(0, 0, MAX_ELEMENTS);

	// This constant defines the amount of data types per edge node
	@Native private static int MAX_ELEMENTS2 = 1000;
	@Native private static int TIMETOLIVE = 3600*12; // half day
	@Native private static int TIMEINTERVAL = 3600*12; // half day
	
	// Key - Value
    private Cache<String, Descriptor> cacheDescriptor = new Cache<String, Descriptor>(TIMETOLIVE, TIMEINTERVAL, MAX_ELEMENTS2);

	@Override
	public void run(ApplicationArguments args) {
		if (args != null && !args.getOptionNames().isEmpty()) {
			try {
				this.loadComponentsPort(args);
				this.CacheVirtualNodePorts();
				this.loadNeededResources(args);
				this.loadEdgenodeConfig(args);

				this.CatalogMgrUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_catalog_manager(), "/catalog");
				this.ManagerApiUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_manager_api(), "/lwcoedgemgr/metrics/put");
				this.getLogger().info( Util.msg("CatalogMgrUrl cache url = ", this.CatalogMgrUrl));
				this.getLogger().info( Util.msg("ManagerApi cache url = ", this.ManagerApiUrl));
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
	public boolean hasResource(Datatype datatype, String... args) throws Exception {
		Descriptor dtype = null;
		try {
			dtype = this.getLoadDescriptor(datatype);
		} catch (Exception e) {
			throw new Exception(Util.msg("[ERROR] ","No Data type descriptor loaded!\n", e.getMessage()));
		}
		if (dtype == null)
			throw new Exception ("[ERROR] No Data type descriptor loaded!");
		ResourcesAvailable currentRes = this.getEdgeNodeResourcesAvailable();
		if (currentRes == null)
			throw new Exception ("[ERROR] No Edge node resources available!");
		
		boolean hasConnectedDevices = this.hasConnectedDevices(dtype);
		return (currentRes.getFreePhysicalMemorySize() >= 
				this.neededRes.get(dtype.getType().toString()).getPhysicalMemorySize()
				) && hasConnectedDevices ;
	}

	@Override
	public boolean hasConnectedDevices(Descriptor datatype, String... args) {
//		this.getLogger().info("Request size ="+args[3]);
		if (datatype.getType() == Type.COMPLEX)
			return true;

		Function<ConnectedDevices,List<String>> deviceKey=p -> Arrays.asList(p.getValue());
		Function<Element,List<String>> elementKey=p -> Arrays.asList(p.getValue());

		EdgeNode en = this.configEdgenode.getEdgeNode();
		
		boolean exists=
				datatype.getElement().stream().map(elementKey)
				.anyMatch(en.getConnectedDevices().stream().map(deviceKey).collect(Collectors.toSet())::contains);

		return exists;
	}
	
	@Override
	public EdgeNode getEdgeNode() {
		return this.configEdgenode.getEdgeNode();
	}

	@Override
	public VirtualNode containerDeploy(Datatype datatype, String... args) throws Exception {
		try {

			LocalDateTime startDeploy = LocalDateTime.now();
			this.getLogger().info("------------------------------");
			this.getLogger().info( Util.msg("Deploying a new Virtual Node [", datatype.getId(), "]"));
			String VNid = Util.msg(getHostName(), ".", datatype.getId());

			Descriptor dtDescriptor = this.getLoadDescriptor(datatype);
			// it is not in the cache
			if (dtDescriptor == null) {
				this.getLogger().info("Virtual Node not deployed!");
				this.getLogger().info(Util.msg("Datatype [", datatype.getId(), "] not available!"));
				this.getLogger().info("------------------------------");
				throw new Exception("[ERROR] Virtual Node not deployed, the Datatype is not available!");
			}
			Integer port = this.getAvailablePort(dtDescriptor.getType().toString());
			VirtualNode vn = new VirtualNode(VNid, dtDescriptor, port);

			this.startContainer(vn);

			if (args.length > 0 ) {
				LocalDateTime finishDeploy = LocalDateTime.now();
				MetricIdentification requestID = new MetricIdentification(args[0]);
				MetricIdentification id = new MetricIdentification(requestID.getExperiment(), "DEPLOY", null, datatype.getId());

				this.getLogger().info( Util.msg("Sending metric [", id.toString(), "] to registry! "));
				try {
					UtilMetric.sendMetricComputationalTime(this.ManagerApiUrl, id.getKey(), id.toString(), startDeploy, finishDeploy);					
				} catch (Exception e) {
					this.getLogger().info( 
							Util.msg("[ERROR] ","Error submitting the metric [", id.toString(), "] to the registry. ", e.getMessage())
					);
				}
			}

			this.getLogger().info("Virtual Node deployed!");
			this.getLogger().info("------------------------------");

			return vn;			
			
		} catch (Exception e) {
			throw new Exception(Util.msg("[ERROR] ","Virtual Node not deployed! ",e.getMessage()));
		}

	}

	@Override
	public void containerStop(VirtualNode vn, String... args) {
		// TODO Auto-generated method stub
		
		try {
			String command = Util.msg("docker stop ", changePointToHyphen(vn.getId()));
			this.getLogger().info( Util.msg("Starting ",command));
			Util.exec(command, 1);

			command = Util.msg("docker rm ", changePointToHyphen(vn.getId()));
			this.getLogger().info( Util.msg("Starting ",command));
			Util.exec(command, 1);

		} catch (Exception e) {
		}
	}

	@Override
	public boolean scaleUp(VirtualNode vn, String... args) {
		// docker update -m 256M [container id]
		Long mem = this.neededRes.get(vn.getDatatype().getType().toString()).getPhysicalMemorySize();
		String unit = this.neededRes.get(vn.getDatatype().getType().toString()).getUnit();
		String command = Util.msg("docker update -m ", mem.toString(), unit, vn.getId());
		this.getLogger().info( Util.msg("Starting ",command));
		try {
			Util.exec(command, 1); // update the container settings memory
			return true;
		} catch (Exception e) {
			return false;			
		} 
	}

	@Override
	public boolean scaleDown(VirtualNode vn, String... args) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getMinimalResources() {
		return this.neededRes.getAll();
	}

	public Resources getNeededResources(String type) {		
		return this.neededRes.get(type);
	}

	private ResourcesAvailable getEdgeNodeResourcesAvailable() throws Exception {
		String enURL = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_monitor(), 
				"/monitor/node/resources");
		try {
			ResponseEntity<ResourcesAvailable> httpResp = 
					Util.sendRequest(enURL, Util.getDefaultHeaders(), HttpMethod.GET, null, ResourcesAvailable.class);
			if (httpResp.hasBody()) {
				return httpResp.getBody();
			}
			throw new Exception("[ERROR] No Edge Node resources available!");
		} catch (Exception e) {
			throw e;
		}
	}

	private Integer getAvailablePort(String key) {
		Integer port = cachePorts.get(key);
		cachePorts.put(key, port+1);
		return port;
	}

	private String getJVMString(VirtualNode vn) {
/*
"java -jar lwcoedge-vn-sensing-0.0.1-SNAPSHOT.jar --server.port=11001 --datasharing.port=10007 --managerapi.port=10500 --logging.file=./log/lwcoedge-vn-sensing-UFRJ.UbicompLab.humidity.log "+
	"--VN={\"id\":\"MPITANGADELL.UFRJ.UbicompLab.temperature\",\"datatype\":{\"id\":{\"where\":\"UFRJ\",\"who\":\"UbicompLab\",\"what\":\"temperature\"},\"description\":null,\"type\":\"SIMPLE\",\"element\":[{\"value\":\"DeviceID1\"}]},\"data\":{},\"neighbors\":[],\"port\":11001}";
*/
		StringBuilder jvmCommand = new StringBuilder();
		String GC = " -XX:+UseG1GC ";

/*		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			GC = " -XX:+UseG1GC ";
		} else {
		   // new garbage collector used in OpenJDK10
			GC = " -XX:+UseShenandoahGC -XX:+UnlockExperimentalVMOptions -XX:ShenandoahUncommitDelay=2000 -XX:ShenandoahGuaranteedGCInterval=10000 ";
		}
*/		
		jvmCommand.append("java -Xmx64m -Xms8m ").append(GC);
		jvmCommand.append(" -jar lwcoedge-vn-sensing-0.0.1-SNAPSHOT.jar "); 
		jvmCommand.append(" --server.port=").append(vn.getPort());
		jvmCommand.append(" --datasharing.port=").append(this.getPorts().getLwcoedge_p2pdatasharing());
		jvmCommand.append(" --managerapi.port=").append(this.getPorts().getLwcoedge_manager_api());
		jvmCommand.append(" --logging.file=./log/lwcoedge-vn-sensing-").append(vn.getDatatype().getDescriptorId()).append(".log");
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			jvmCommand.append(" \"--VN=").append( Util.escape(vn.toString()) ).append("\"");
		} else {
			jvmCommand.append(" --VN=").append( vn.toString() );
		}
		return jvmCommand.toString();
	}

	private String changePointToHyphen(String str) {
		return str.replace(".", "_");
	}

	private String getDockerString(VirtualNode vn) {

//docker run -p 11001:11001 --name EN1.UFRJ.UbicompLab.temperature --hostname EN1.UFRJ.UbicompLab.temperature lw_coedge_simple 
//"--server.port=11001" "--datasharing.port=10007" "--logging.file=./log/lwcoedge-vn-sensing.log" 
//"--VN={"id":"en1.UFRJ.UbicompLab.temperature","datatype":{"id":{"where":"UFRJ","who":"UbicompLab","what":"temperature"},"description":null,"type":"SIMPLE","element":[{"value":"DeviceID1"}]},"data":{},"neighbors":[]}"

		StringBuilder dockerCommand = new StringBuilder();
		dockerCommand.append("ssh -oStrictHostKeyChecking=no $USER@").append(vn.getHostName()).append(" -i /home/$USER/.ssh/id_rsa \"");
		dockerCommand.append("docker run d -p ").append(vn.getPort()).append(":").append(vn.getPort());
		dockerCommand.append(" --name ").append( changePointToHyphen(vn.getId()) );
		dockerCommand.append(" --hostname ").append( changePointToHyphen(vn.getId()) );
		dockerCommand.append(" lw_coedge_").append(vn.getDatatype().getType().toString().toLowerCase());
		dockerCommand.append(" \"--server.port=").append(vn.getPort()).append("\"");
		dockerCommand.append(" \"--datasharing.port=").append(this.getPorts().getLwcoedge_p2pdatasharing()).append("\"");
		dockerCommand.append(" \"--managerapi.port=").append(this.getPorts().getLwcoedge_manager_api()).append("\"");
		dockerCommand.append(" \"--logging.file=./log/lwcoedge-vn-sensing-").append(vn.getDatatype().getDescriptorId()).append(".log\"");
		dockerCommand.append(" \"--VN=").append( Util.escape(vn.toString()) );
		dockerCommand.append("\"");
		return dockerCommand.toString();
	}

	private void startContainer(VirtualNode vn) throws Exception {
		String command = this.getJVMString(vn);
		this.getLogger().info( Util.msg("Starting ",command));
		Util.exec(command, 5); //// start container and wait 5 seconds
		String vnURL = getUrl("http://", vn.getHostName(), vn.getPort(), "/vnsensing/isalive");
		int i=0;
		boolean vnStart = false;
		this.getLogger().info("Waiting for the Virtual Node start!");
		for(; i<60;i++) { // trying to access the VN for 60 seconds
			try {
				this.getLogger().info(Util.msg("Verifying if the Virtual Node is alive [",String.valueOf(i),"]..."));
				ResponseEntity<Boolean> b = 
						Util.sendRequest(vnURL, Util.getDefaultHeaders(), HttpMethod.GET, null, Boolean.class);
				if (b.hasBody() && b.getBody()) {
					vnStart = true;
					break;
				}
			} catch (ResourceAccessException c) {
				getLogger().info("Communication fail!");
			} catch (HttpServerErrorException e) {
				getLogger().info(e.getMessage());
			} catch (Exception e) {
				getLogger().info(e.getMessage());
			}
			Thread.sleep(1000);
		}
		if (i>=60 || !vnStart) {
			this.getLogger().info("[ERROR] Virtual Node initialization failed!");
			throw new RuntimeException("[ERROR] Virtual Node initialization failed!");
		}
		//this.getLogger().info("Virtual Node deployed!");
	}
	
	private Descriptor getLoadDescriptor(Datatype datatype) throws Exception {
		Descriptor dtDescriptor = cacheDescriptor.get(datatype.getId());
		// it is not in the cache
		if (dtDescriptor == null) {
			ResponseEntity<Descriptor> httpResp = 
					Util.sendRequest( Util.msg(this.CatalogMgrUrl, "/descriptor"), Util.getDefaultHeaders(), HttpMethod.POST, datatype, Descriptor.class);

			if (httpResp.hasBody()) {
				dtDescriptor = httpResp.getBody();
				dtDescriptor.setDescription(null);
				cacheDescriptor.put(datatype.getId(), dtDescriptor);
			}
		}
		return dtDescriptor;
	}

	private void loadNeededResources(ApplicationArguments args) throws Exception {
		this.getLogger().info("Loading the resource settings for the data types...");
		String fileName = args.getOptionValues("NeededResources").get(0);
		ObjectMapper objectMapper = new ObjectMapper();
		ConfigNeededResources neededResources = objectMapper.readValue(new File(fileName), ConfigNeededResources.class);
		for (NeededResources dtTypesRes : neededResources.getNeededResources()) {
			neededRes.put(dtTypesRes.getType(), dtTypesRes.getResources());
		}
		this.getLogger().info(neededResources.toString());
	}

	@Override
	public void loadEdgenodeConfig() throws Exception {
		this.getLogger().info("Loading the edge node settings...");
		ObjectMapper objectMapper = new ObjectMapper();
		this.configEdgenode = objectMapper.readValue(new File(this.fileName), ConfigEdgenode.class);
		this.getLogger().info(this.configEdgenode.toString());
		this.getLogger().info("Edge node settings loaded.");

		try {
			sendToBroker(this.configEdgenode);
		} catch (Exception e) {
			this.getLogger().info(Util.msg("[ERROR] ",e.getMessage()));
		}		
	}
	
	private void loadEdgenodeConfig(ApplicationArguments args) throws Exception {
		this.fileName = args.getOptionValues("EdgenodeConfig").get(0);
		this.loadEdgenodeConfig();
	}

	private void sendToBroker(ConfigEdgenode config) throws Exception {
		this.getLogger().info("Sending the edge node settings to Broker...");
	}
	
	private void CacheVirtualNodePorts() {
		this.getLogger().info("Configuring the initial communication port for each type of Virtual Node....");
		this.cachePorts.put(Type.SIMPLE.toString(), this.getPorts().getLwcoedge_vn_sensing());
		this.cachePorts.put(Type.ACTUATION.toString(), this.getPorts().getLwcoedge_vn_actuation());
		this.cachePorts.put(Type.COMPLEX.toString(), this.getPorts().getLwcoedge_vn_datahandling());

		this.getLogger().info(Util.msg(Type.SIMPLE.toString(), " - Port: ", this.getPorts().getLwcoedge_vn_sensing().toString()));
		this.getLogger().info(Util.msg(Type.ACTUATION.toString(), " - Port: ", this.getPorts().getLwcoedge_vn_actuation().toString()));
		this.getLogger().info(Util.msg(Type.COMPLEX.toString()," - Port: ", this.getPorts().getLwcoedge_vn_datahandling().toString()));
		this.getLogger().info("Configuration finished.");
	}

}
