package br.edu.ufrj.lwcoedge.p2pcollaboration.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import br.edu.ufrj.lwcoedge.core.cache.Cache;
import br.edu.ufrj.lwcoedge.core.interfaces.IP2Prov;
import br.edu.ufrj.lwcoedge.core.model.Datatype;
import br.edu.ufrj.lwcoedge.core.model.Descriptor;
import br.edu.ufrj.lwcoedge.core.model.EdgeNode;
import br.edu.ufrj.lwcoedge.core.model.Element;
import br.edu.ufrj.lwcoedge.core.model.NeededResourcesCache;
import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.model.Resources;
import br.edu.ufrj.lwcoedge.core.model.ResourcesAvailable;
import br.edu.ufrj.lwcoedge.core.model.Type;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;
import br.edu.ufrj.lwcoedge.core.model.VirtualNodeInstances;
import br.edu.ufrj.lwcoedge.core.service.AbstractService;
import br.edu.ufrj.lwcoedge.core.service.SendMetricService;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.p2pcollaboration.service.internal.model.EdgeNodeResourcesandLatency;

@Service
@ComponentScan("br.edu.ufrj.lwcoedge.core")
public class P2PCollaborationService extends AbstractService implements IP2Prov {

	@Autowired
	SendMetricService metricService;
	
	// This constant defines the number of data types per edge node
	private final int MAX_ELEMENTS = 50;


	private final int TIMETOLIVE = 3600*12; // half day
	private final int TIMEINTERVAL = 3600*12; // half day	

	// Key - Value
    private Cache<String, Descriptor> cacheDescriptor = new Cache<String, Descriptor>(TIMETOLIVE, TIMEINTERVAL, MAX_ELEMENTS);
   
    private EdgeNode edgeNode;
    private NeededResourcesCache neededRes = new NeededResourcesCache();
    
	private String catalogMgrUrl, managerApiUrl;
	
	private boolean enableCollaboration = true;
		
	@Override
	public void appConfig(ApplicationArguments args) throws Exception {
		this.getLogger().info("LW-CoEdge loading application settings...\n");
		if (args != null && !args.getOptionNames().isEmpty()) {
			try {
				this.loadComponentsPort(args);

				this.catalogMgrUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_catalog_manager(), "/catalog");
				this.managerApiUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_manager_api(), "/lwcoedgemgr/metrics/put");

				this.edgeNodeConfig();
				this.neededResources();
				
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
	public void neededResources() throws Exception {
		this.getLogger().info("Loading Data types needed resources config...");
		Arrays.asList(Type.values())
		  .forEach(type -> this.getNeededResources(type.toString()));
		this.getLogger().info("Data types needed resources config loaded! ");		
	}
	
	@Override
	public void edgeNodeConfig() throws Exception {
		this.getLogger().info("Loading edge node config...");
		this.edgeNode = this.getEdgeNodeConfig();
		if (this.edgeNode == null) {
			throw new Exception("No edge node config available!");
		}
		this.getLogger().info("Edge node config loaded: {}", this.edgeNode.toString() );
	}

	private void getNeededResources(String type) {
		try {
			String egeNodeManagerUrl = 
				this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_edgenode_manager(),
						Util.msg("/edgenodemanager/datatype/neededresources/", type));
			ResponseEntity<Resources> httpResp = 
					Util.sendRequest(egeNodeManagerUrl, Util.getDefaultHeaders(), HttpMethod.GET, null, Resources.class);
			this.neededRes.put(type, httpResp.getBody());
			this.getLogger().info("Loading [{}] : {}", type, httpResp.getBody().toString());
		} catch (Exception e) {
			this.getLogger().error("[ERROR] Loading [{}] : ", type, e.getMessage());
		}
	}

	private EdgeNode getEdgeNodeConfig() throws Exception {
		String egeNodeManagerUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_edgenode_manager(), "/edgenodemanager/edgenode/config");
		ResponseEntity<EdgeNode> httpResp = 
				Util.sendRequest(egeNodeManagerUrl, Util.getDefaultHeaders(), HttpMethod.GET, null, EdgeNode.class);
		if (httpResp.hasBody()) {
			return httpResp.getBody();
		}
		return null;
	}

	@Override
	public void sendToNeighborNode(Request request, String... args) throws Exception {
		this.getLogger().debug("[SendToNeighborNode] Request received [{}]", args[0]);

		if (!enableCollaboration) {
			this.getLogger().info( "[SendToNeighborNode] The collaboration process is inactive!" );
			throw new Exception("The collaboration process is inactive!");
		}

		try {
			// if the Proof-of-Concept sends the request, then allow the metric registration.
			final boolean sendMetric = !args[2].equals("R");
			
			LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();

			//args -> RequestID, StartDateTime, ExperimentID, StartComm, RequestSize, TimeSpentWithP2P
			headers.put("RequestID", args[0]);
			headers.put("StartDateTime", args[1]);
			headers.put("ExperimentID", args[2]);
			headers.put("StartComm", args[3]);
			headers.put("RequestSize", args[4]);
			headers.put("TimeSpentWithP2P", args[5]);

			//The date & time when the request started (come from resource allocator)
			final LocalDateTime start = LocalDateTime.parse(headers.get("StartDateTime"));

			final LocalDateTime startDTSelectNeighborNode = LocalDateTime.now();
			final long spentTimeToStartP2P = Duration.between(start, startDTSelectNeighborNode).toMillis();

			this.getLogger().debug("[SendToNeighborNode] Spent time to receive the request and start the P2P [{}]",spentTimeToStartP2P);

			// search a neighbor node
			EdgeNode en = this.getNeighborNode(request, headers.get("ExperimentID"), sendMetric);
			
			if (en == null) {
				throw new Exception("[SendToNeighborNode] No neighboring node available!");
			}
			String resourceAllocatorUrl = this.getUrl("http://", en.getIp(), this.getPorts().getLwcoedge_resourceallocator(), 
					"/resourceallocator/handlerequest");

			final LocalDateTime finalDTSelectNeighborNode = LocalDateTime.now();
			final long timeToSelectNeighborNode = Duration.between(startDTSelectNeighborNode, finalDTSelectNeighborNode).toMillis();
			
			this.getLogger().debug( 
					"[SendToNeighborNode] The neighbor node selected. Start [{}] End [{}] Duration [{}]",
					startDTSelectNeighborNode.toString(), finalDTSelectNeighborNode.toString(), Long.toString(timeToSelectNeighborNode));
			
			// Calculating the communication latency between the current edge node and the neighbor edge node
//			long latency = this.ping(en.getIp(), 2, Integer.parseInt(headers.getOrDefault("RequestSize", "32")));
//			headers.put("CommLatency", Long.toString(latency));

			this.getLogger().debug("[SendToNeighborNode] Sending the request [{}] to the Resource allocator url = {}",
					headers.get("RequestID") , resourceAllocatorUrl);

//			final LocalDateTime finalDateCommWithLatency = finalDTSelectNeighborNode.plus(latency, ChronoField.MILLI_OF_DAY.getBaseUnit());

			final long OldTimeSpentWithP2P = Long.parseLong(headers.getOrDefault("TimeSpentWithP2P", "0"));		
			final long timeSpentWithP2P = (spentTimeToStartP2P+timeToSelectNeighborNode) + OldTimeSpentWithP2P;			
			headers.put("TimeSpentWithP2P", Long.toString(timeSpentWithP2P));

			// Calculating the communication latency between the current edge node and the neighbor edge node
			final LocalDateTime startSendRequest = LocalDateTime.now();	
			headers.put("startP2PSendRequest", startSendRequest.toString());
			Util.sendRequest(resourceAllocatorUrl, Util.getDefaultHeaders(headers), HttpMethod.POST, request, Void.class, 200);
			final LocalDateTime finalDateCommWithLatency = LocalDateTime.now();
			
			final long durationSpentFW = Duration.between(start, finalDateCommWithLatency).toMillis();

			this.getLogger().debug( 
					"[SendToNeighborNode] Request sent to neighbor node."
					+ " Duration to handle the request (from dt received request to forward) [{}]"
					+ " - Old TimeSpentWithP2P [{}] - TimeSpentWithP2P [{}].", 
					Long.toString(durationSpentFW), Long.toString(OldTimeSpentWithP2P), Long.toString(timeSpentWithP2P)
			);
			if (sendMetric) {
				//TIME_SELECT_NB
				metricService.sendMetricSummary(managerApiUrl, headers.get("ExperimentID"), "TIME_SELECT_NB", request.getDatatype().getId(), startDTSelectNeighborNode, finalDTSelectNeighborNode);

				//M4
				metricService.sendMetric(managerApiUrl, headers.get("ExperimentID"), "REQ_SENTTO_NB", request.getDatatype().getId());

				//M5
				metricService.sendMetricSummary(managerApiUrl, headers.get("ExperimentID"), "TIME_SPENT_FW", request.getDatatype().getId(), start, finalDateCommWithLatency);

				//M6 Communication time (edge to edge).
				if (headers.get("StartComm") != null) {
					metricService.sendMetricSummary(managerApiUrl, headers.get("ExperimentID"), "COMM_TIME", request.getDatatype().getId(), startSendRequest, finalDateCommWithLatency);
				}
			}
		} catch (Exception e) {
			this.getLogger().error("[ERROR] Error sending the request to the Resource provisioner on the neighbor node.\nCause: {} ",e.getMessage());
			throw new Exception(e.getMessage());
		}
	}

	private EdgeNode getNeighborNode(Request request, String experimentID, boolean sendMetric) throws Exception {
		Descriptor dtDescriptor = cacheDescriptor.get(request.getDatatype().getId());
		// Descriptor is not in the cache
		if (dtDescriptor == null) {
			ResponseEntity<Descriptor> httpResp = 
					Util.sendRequest( Util.msg(this.catalogMgrUrl, "/descriptor"), Util.getDefaultHeaders(), HttpMethod.POST, request.getDatatype(), Descriptor.class);
			dtDescriptor = httpResp.getBody();
			if (dtDescriptor == null) {
				throw new Exception ("No datatype descriptor available!");
			}
			cacheDescriptor.put(request.getDatatype().getId(), dtDescriptor);
		}

		EdgeNode bestNeighborNode = null;
		ArrayList<EdgeNode> neighborhood = this.edgeNode.getNeighborhood();
		ArrayList<EdgeNode> candidateNeighborNodes = new ArrayList<EdgeNode>();
		
		for (EdgeNode neighborNode : neighborhood) {
			boolean hasConnectedDevices = neighborNode.hasConnectedDevices(dtDescriptor);
			this.getLogger().debug(
					"[SendToNeighborNode] Verifying the connected devices. Edge Node [{}] has connected devices? {}", 
					neighborNode.getHostName(), Boolean.toString(hasConnectedDevices)
			);
			if (hasConnectedDevices) {
				candidateNeighborNodes.add(neighborNode);
			}
		}
		bestNeighborNode = this.bestResource(dtDescriptor, candidateNeighborNodes, experimentID, request.getDatatype(), sendMetric);
		this.getLogger().debug("[SendToNeighborNode] Best Neighbor Node: {}", (bestNeighborNode == null) ? "No edge node available" : bestNeighborNode.getHostName());
		return bestNeighborNode;
	}

	private EdgeNode bestResource(Descriptor datatype, ArrayList<EdgeNode> candidateNeighborNodes, String experimentID, Datatype datatypeId, boolean sendMetric) {
		if (candidateNeighborNodes.isEmpty()) {
			return null;
		}
		Long needMem = this.neededRes.get(datatype.getType().toString()).getPhysicalMemorySize();
		ArrayList<EdgeNodeResourcesandLatency> arrayRes = new ArrayList<EdgeNodeResourcesandLatency>();		
		for (EdgeNode edgeNode : candidateNeighborNodes) {
			try {
				EdgeNodeResourcesandLatency edgeNodeRes =
						this.getEdgeNodeResourcesAvailableandLatency(edgeNode, experimentID, datatypeId, sendMetric);
				
				if (needMem <= edgeNodeRes.getResources().getFreePhysicalMemorySize()) {
					arrayRes.add(edgeNodeRes);
				}
				
			} catch (Exception e) {
				this.getLogger().error("[ERROR1] {}",e.getMessage());
			}
		}
		if (arrayRes.isEmpty()) return null;
		// ordering the edge node with the best resources to the first position
		Collections.sort(arrayRes);
		for (EdgeNodeResourcesandLatency edgeNodeResourcesandLatency : arrayRes) {
			this.getLogger().debug("[BestResource] {}", edgeNodeResourcesandLatency.toString());
		}
		return arrayRes.get(0).getEdgenode();
	}

	private EdgeNodeResourcesandLatency getEdgeNodeResourcesAvailableandLatency(EdgeNode en, String experimentID, Datatype datatype, boolean sendMetric) throws Exception {
		this.getLogger().debug("[SendToNeighborNode] Checking available resources of the edge node [{}]...", en.getHostName());
		String monitorURL = 
				this.getUrl("http://", en.getIp(), this.getPorts().getLwcoedge_monitor(), "/monitor/node/resources");
		this.getLogger().debug("[SendToNeighborNode] Accessing the monitor at URL [{}]", monitorURL);
		try {
			LocalDateTime startRequest = LocalDateTime.now();
			ResponseEntity<ResourcesAvailable> httpResp = 
					Util.sendRequest(monitorURL, Util.getDefaultHeaders(), HttpMethod.GET, null, ResourcesAvailable.class, 250);
			LocalDateTime finalRequest = LocalDateTime.now();
			long latency = Duration.between(startRequest, finalRequest).toMillis();

			if (httpResp.hasBody()) {
				if (sendMetric) {
					//data consumed to know whether a node has resources
					// size of request operation 0 bytes
					Long valueOf = 0l;
					try {
						valueOf = 
								Integer.valueOf(0)+
								Util.getObjectSize(httpResp.getHeaders()) +
								Util.getObjectSize(httpResp.getBody());						
					} catch (Exception e) {
						this.getLogger().error("[getEdgeNodeResourcesAvailableandLatency] Error to get size of request to access Monitor {}", e.getMessage());
					}
					metricService.sendMetricSummaryValue(managerApiUrl, experimentID, "DT_REQSENT_NB_2", datatype.getId(), valueOf);
				}
				EdgeNodeResourcesandLatency raNew = new EdgeNodeResourcesandLatency(en, httpResp.getBody(), latency);
				return raNew;
			}
			throw new Exception("No Edge Node resources available! ["+en.getHostName()+"]");
		} catch (Exception e) {
			this.getLogger().error(e.getMessage());
			throw new Exception("No Edge Node resources available! ["+en.getHostName()+"]");
		}
	}


	@Async("ProcessExecutor-P2P")
	@Override
	public void registerVNtoDataSharing(VirtualNode newVirtualNode, String... args) throws Exception {
		try {
			if (newVirtualNode.getDatatype().getType() == Type.COMPLEX) {
				this.getLogger().info( "[RegisterVNtoDataSharing] The data type of type Complex is not supported to the collaboration!" );
				return;
			}
/*
			if (!enableCollaboration) {
				this.getLogger().info( "[RegisterVNtoDataSharing] The collaboration process is inactive!" );
				return;
			}
*/
			LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();

			//args -> requestID, startDateTime, experimentID, requestSize
			headers.put("RequestID", args[0]);
			headers.put("StartDateTime", args[1]);
			headers.put("ExperimentID", args[2]);
			headers.put("RequestSize", args[3]);
			// if the Proof-of-Concept sends the request, then allow the metric registration.
			final boolean sendMetric = !args[2].equals("R");

			if (sendMetric) {
				//DT_SHARING
				//data consumed to received the answer
				Long valueOf = Long.parseLong(headers.getOrDefault("RequestSize", "0"));
				metricService.sendMetricSummaryValue(managerApiUrl, headers.get("ExperimentID"), "DT_SHARING_1", newVirtualNode.getDatatype().getDescriptorId(), valueOf);
			}

			ArrayList<VirtualNode> vnsUpdated = new ArrayList<VirtualNode>();
						
			try {
				this.getLogger().debug("Registering VN [{}] inside of the same node to data sharing ", newVirtualNode.getId());
				// inside of the same node
				String VNInstanceCacheUrl = 
					this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_vn_instancecache(),
						"/vninstancecache/cache/list/instances");

				ResponseEntity<VirtualNodeInstances> httpResp =
						Util.sendRequest(VNInstanceCacheUrl, Util.getDefaultHeaders(), HttpMethod.GET, null, VirtualNodeInstances.class);
				ArrayList<VirtualNode> virtualNodeInstances = httpResp.getBody().getVirtualNodeInstances();

				for (VirtualNode vn : virtualNodeInstances) {
					this.verifyIntersection(newVirtualNode, vn, vnsUpdated);
				}

				if (sendMetric) {
					//DT_SHARING
					//data consumed to received the answer
					Long valueOf = Util.getObjectSize(httpResp.getHeaders()) +
							Util.getObjectSize(httpResp.getBody());
					metricService.sendMetricSummaryValue(managerApiUrl, headers.get("ExperimentID"), "DT_SHARING_2", newVirtualNode.getDatatype().getDescriptorId(), valueOf);
				}

			} catch (Exception e) {
				this.getLogger().error("[ERROR] Registering VN inside of the same node to data sharing {}", e.getMessage());
			}

			//Verifying in the neighboring nodes
			this.getLogger().debug("Registering VN [{}] to data sharing in the neighboring nodes.", newVirtualNode.getId());

			ArrayList<EdgeNode> neighborhood = this.edgeNode.getNeighborhood();
			for (EdgeNode neighborNode : neighborhood) {
				try {
					String neighborVNInstanceCacheUrl =
							this.getUrl("http://", neighborNode.getIp(), this.getPorts().getLwcoedge_vn_instancecache(), 
								"/vninstancecache/cache/list/instances");

					ResponseEntity<VirtualNodeInstances> httpResp =
							Util.sendRequest(neighborVNInstanceCacheUrl, Util.getDefaultHeaders(), HttpMethod.GET, null, VirtualNodeInstances.class, 500);

					if (sendMetric) {
						//DT_SHARING
						//data consumed to received the answer
						Long valueOf = Util.getObjectSize(httpResp.getHeaders()) +
								Util.getObjectSize(httpResp.getBody());
						metricService.sendMetricSummaryValue(managerApiUrl, headers.get("ExperimentID"), "DT_SHARING_3", newVirtualNode.getDatatype().getDescriptorId(), valueOf);
					}

					ArrayList<VirtualNode> virtualNodeInstances = httpResp.getBody().getVirtualNodeInstances();
					for (VirtualNode neighBorVn : virtualNodeInstances) {
						this.verifyIntersection(newVirtualNode, neighBorVn, vnsUpdated);
					}
					
				} catch (Exception e) {
					this.getLogger().error("[ERROR] During the register of the VN  to data sharing in the neighbor node [{}] {}",neighborNode.getIp() ,e.getMessage());
				}
			}
			// Re-register the updated Virtual nodes into the Cache
			for (VirtualNode virtualNode : vnsUpdated) {
				this.register(virtualNode);
			}

		} catch (Exception e) {
			this.getLogger().error("[ERROR] During the register of the VN  to data sharing {}",e.getMessage());
		}
	}


	private void verifyIntersection(VirtualNode newVirtualNode, VirtualNode virtualNode, ArrayList<VirtualNode> vnsUpdated) {
		if (newVirtualNode.getId().equals(virtualNode.getId()))
			return;

		Function<Element,List<String>> elementKey=p -> Arrays.asList(p.getValue());

		boolean exists= newVirtualNode.getDatatype().getElement().stream().map(elementKey)
				.anyMatch(virtualNode.getDatatype().getElement().stream().map(elementKey).collect(Collectors.toSet())::contains);

		if (!exists) {
			this.getLogger().info("No Intersection end devices!");
			return;			
		}

		if (!newVirtualNode.getNeighbors().contains(virtualNode.getId())) {
			newVirtualNode.getNeighbors().add(virtualNode.getId());			
		}
		
		if(!virtualNode.getNeighbors().contains(newVirtualNode.getId())) {
			virtualNode.getNeighbors().add(newVirtualNode.getId());
		}
		
		vnsUpdated.add(newVirtualNode);
		vnsUpdated.add(virtualNode);
		this.getLogger().info("End device match to the Virtual Node confirmed!");
		this.getLogger().info(newVirtualNode.toString());
	}
	
	private void register(VirtualNode vn) throws Exception {
		//Updating the new instance of the VN into the cache
		String url;
		try {
			this.getLogger().info("------------------------------");
			this.getLogger().info("Updating the Virtual Node instance {} into the repository...", vn.getId());
			String hostName = this.getHostName();
			if (!vn.getHostName().equals(this.getHostName())) {
				hostName = vn.getHostName();
			}
			url = this.getUrl("http://", hostName, this.getPorts().getLwcoedge_vn_instancecache(), "/vninstancecache/register");
			Util.sendRequest(url, Util.getDefaultHeaders(), HttpMethod.POST, vn, Void.class);
			
			this.getLogger().info("Updating the neighbors into the Virtual Node instance {}...", vn.getId());
			//update VN neighboring
			url = this.getUrl("http://", hostName, vn.getPort(), "/vnsensing/neighbor/register");
			Util.sendRequest(url, Util.getDefaultHeaders(), HttpMethod.POST, vn.getNeighbors(), Void.class);		
			
			this.getLogger().info("VN -> {}", vn.toString());
			this.getLogger().info("VN instance updated!");
		} catch (Exception e) {
			this.getLogger().error( "[ERROR] VN instance not updated into the cache!\n {}", e.getMessage());
		}
	}

	@Override
	public synchronized void setCollaboration(boolean enable) {
		this.getLogger().info("Configuring the Collaboration process...");
		this.enableCollaboration = enable;
		if (enable) {
			this.getLogger().info("The Collaboration process is enabled!");
		} else {
			this.getLogger().info("The Collaboration process is disabled!");
		}
	}
}
