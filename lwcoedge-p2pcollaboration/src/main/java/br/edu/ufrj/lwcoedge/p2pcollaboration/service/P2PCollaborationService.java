package br.edu.ufrj.lwcoedge.p2pcollaboration.service;

import java.lang.annotation.Native;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.IcmpPingUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.edu.ufrj.lwcoedge.core.cache.Cache;
import br.edu.ufrj.lwcoedge.core.interfaces.IP2Prov;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricIdentification;
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
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.core.util.UtilMetric;

@Service
public class P2PCollaborationService extends AbstractService implements ApplicationRunner, IP2Prov {

	// This constant defines the amount of data types per edge node
	@Native private static int MAX_ELEMENTS2 = 5000;
	@Native private static int TIMETOLIVE = 3600*12; // half day
	@Native private static int TIMEINTERVAL = 3600*12; // half day	
	// Key - Value
    private Cache<String, Descriptor> cacheDescriptor = new Cache<String, Descriptor>(TIMETOLIVE, TIMEINTERVAL, MAX_ELEMENTS2);

    private EdgeNode edgeNode;
    private NeededResourcesCache neededRes = new NeededResourcesCache();
    
	private String catalogMgrUrl, managerApiUrl;
	
	private boolean enableCollaboration = true;
	
	final IcmpPingRequest pingRequest = IcmpPingUtil.createIcmpPingRequest();
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (args != null && !args.getOptionNames().isEmpty()) {
			try {
				this.loadComponentsPort(args);

				this.catalogMgrUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_catalog_manager(), "/catalog");
				this.managerApiUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_manager_api(), "/lwcoedgemgr/metrics/put");

				this.edgeNodeConfig();
				this.neededResources();
				
			} catch (Exception e) {
				this.getLogger().info(e.getMessage());
				System.exit(-1);
			}
		} else {
			this.getLogger().info("No application settings founded!");
			System.exit(-1);
		}		
		
	}
	
	public void neededResources() throws Exception {
		this.getLogger().info("Loading Data types needed resources config...");
		Arrays.asList(Type.values())
		  .forEach(type -> this.getNeededResources(type.toString()));
		this.getLogger().info("Data types needed resources config loaded! ");		
	}
	
	public void edgeNodeConfig() throws Exception {
		this.getLogger().info("Loading edge node config...");
		this.edgeNode = this.getEdgeNodeConfig();
		if (this.edgeNode == null) {
			throw new Exception("No edge node config available!");
		}
		this.getLogger().info( Util.msg("Edge node config loaded: ", this.edgeNode.toString()) );
	}

	private void getNeededResources(String type) {
		try {
			String egeNodeManagerUrl = 
				this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_edgenode_manager(),
						Util.msg("/edgenodemanager/datatype/neededresources/", type));
			ResponseEntity<Resources> httpResp = 
					Util.sendRequest(egeNodeManagerUrl, Util.getDefaultHeaders(), HttpMethod.GET, null, Resources.class);
			this.neededRes.put(type, httpResp.getBody());
			this.getLogger().info( Util.msg("Loading [", type, "] : ", httpResp.getBody().toString()));
		} catch (Exception e) {
			this.getLogger().info(Util.msg("[ERROR] ","Loading [", type, "] : ",e.getMessage()));
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
		if (!enableCollaboration) {
			this.getLogger().info( "[SendToNeighborNode] The collaboration process is inactive!" );
			throw new Exception("[SendToNeighborNode] The collaboration process is inactive!");
		}

		try {
			// if the Proof-of-Concept sends the request, then allow the metric registration.
			final boolean sendMetric = !args[2].equals("R");

			EdgeNode en = this.getNeighborNode(request, args[2], sendMetric);
			if (en == null) {
				throw new Exception("[SendToNeighborNode] No neighboring node available!");
			}

			String resourceAllocatorUrl = this.getUrl("http://", en.getIp(), this.getPorts().getLwcoedge_resourceallocator(), 
					"/resourceallocator/handlerequest");
			
			LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();

			//args -> RequestID, StartDateTime, ExperimentID, StartCommDateTime, StartP2PDateTime, RequestSize
			headers.put("RequestID", args[0]);
			headers.put("StartDateTime", args[1]);
			headers.put("ExperimentID", args[2]);
			headers.put("StartCommDateTime", args[3]);
			headers.put("StartP2PDateTime", args[4]);
			headers.put("RequestSize", args[5]);

			// Calculating the communication latency between the current edge node and the neighbor edge node
			long latency = this.ping(en.getIp(), 4, Integer.parseInt(headers.getOrDefault("RequestSize", "32")));
			headers.put("CommLatency", Long.toString(latency));

			this.getLogger().info( Util.msg("[SendToNeighborNode] Sending the request [", headers.get("RequestID") ,"] to the Resource allocator url = ", resourceAllocatorUrl));
			Util.sendRequest(resourceAllocatorUrl, Util.getDefaultHeaders(headers), HttpMethod.POST, request, Void.class);

			if (sendMetric) {
				//TIME_REQ
				final MetricIdentification M4 = new MetricIdentification(headers.get("ExperimentID"), "REQ_SENTTO_NB", null, request.getDatatype().getId());
				try {
					send_Metric(M4,1);
				} catch (Exception e) {
					String msg = Util.msg("[ERROR] ","Error submitting the metric [", M4.toString(), "] to the registry.", e.getMessage());
					this.getLogger().info(msg);
				}

				//M5
				final MetricIdentification M5 = new MetricIdentification(headers.get("ExperimentID"), "TIME_SPENT_FW", null, request.getDatatype().getId());
				final LocalDateTime start = LocalDateTime.parse(args[1]);
				final LocalDateTime finish = LocalDateTime.now();
				try { // The metric M5 values (id and start date time) come from the HttpHeader
					send_Metric(M5, start, finish);
				} catch (Exception e2) {
					this.getLogger().info(Util.msg("[ERROR] ","Registering the metric [", M5.toString(), "]\n", 
							e2.getCause().getMessage()));
				}
				//Communication time (edge to edge).
				if (headers.get("startCommDateTime") != null) {
					final MetricIdentification M6 = new MetricIdentification(headers.get("ExperimentID"), "COMM_TIME", null, request.getDatatype().getId());
					final LocalDateTime startDateComm = LocalDateTime.now();
					final LocalDateTime finishDateCommWithLatency = startDateComm.plus(latency, ChronoField.MILLI_OF_DAY.getBaseUnit());

					try { // The metric M6 values (id, date and date with latency) come from the HttpHeader
						send_Metric(M6, startDateComm, finishDateCommWithLatency);
					} catch (Exception e2) {
						this.getLogger().info(Util.msg("[ERROR] ","Registering the metric [", M6.toString(), "]\n", 
								e2.getCause().getMessage()));
					}
				}
			}
		} catch (Exception e) {
			String msg = Util.msg("[ERROR] ","Error sending the request to the Resource provisioner on the neighbor node.\nCause: ",e.getMessage());
			this.getLogger().info( msg );
			throw new Exception(e.getMessage());
		}
	}

	private void registerRequestSizeMetric(MetricIdentification id, Long valueOf) throws Exception {
		new Thread(()-> {
			//Bandwidth consumed
			this.getLogger().info( Util.msg("Sending metric ", id.toString(), " to registry..."));
			try {
				UtilMetric.sendMetricSummaryValue(this.managerApiUrl, id.getKey(), id.toString(), valueOf);
			} catch (Exception e) {
				String msg = Util.msg("[ERROR] ","Error submitting the metric [", id.toString(), "] to the registry.", e.getMessage());
				this.getLogger().info(msg);
			}
		}).start();
	}

	private void send_Metric(MetricIdentification metricId, Integer value) throws Exception {
		new Thread( ()->{
			this.getLogger().info( Util.msg("Sending metric ", metricId.toString(), " to registry..."));
			try {
				UtilMetric.sendMetric(this.managerApiUrl, metricId.getKey(), metricId.toString(), value);
			} catch (Exception e) {
				this.getLogger().info( 
					Util.msg("[ERROR] ","Submitting the metric [", metricId.toString(), "] to the registry.\n", e.getMessage())
				);
			}
		}).start();
	}

	private void send_Metric(MetricIdentification metricID, LocalDateTime start, LocalDateTime finish) throws Exception {
		new Thread( ()->{
			this.getLogger().info( Util.msg("Sending metric ", metricID.toString(), " to registry..."));
			try {
				// Summary
				UtilMetric.sendMetricSummary(this.managerApiUrl, metricID.getKey(), metricID.getSummaryKey(), start, finish);
			} catch (Exception e) {
				this.getLogger().info( 
						Util.msg("[ERROR] ","Submitting the metric [", metricID.toString(), "] to the registry.\n", e.getMessage())
				);
			}			
		}).start();
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
		for (EdgeNode neighborNode : neighborhood) {
			String neighborNodeUrl =
					this.getUrl("http://", neighborNode.getIp(), this.getPorts().getLwcoedge_edgenode_manager(), 
							"/edgenodemanager/hasConnectedDevices");
			boolean hasConnectedDevices = false;
			try {
				ResponseEntity<Boolean> httpResp = 
						Util.sendRequest(neighborNodeUrl, Util.getDefaultHeaders(), HttpMethod.POST, dtDescriptor, Boolean.class);
				if (httpResp.hasBody()) {
					hasConnectedDevices = httpResp.getBody();
					
					if (sendMetric) {

						//data consumed to know whether a node has resources
						try {
							MetricIdentification id = new MetricIdentification(experimentID, "DT_REQSENT_NB_1", null, request.getDatatype().getId());

							// size of request operation 391 bytes
							
							Long valueOf = Integer.valueOf(391)+
									Util.getObjectSize(httpResp.getHeaders()) +
									Util.getObjectSize(httpResp.getBody());

							registerRequestSizeMetric(id, valueOf);
						} catch (Exception e) {
							this.getLogger().info(e.getMessage());
						}

					}

				}
			} catch (Exception e) {
				hasConnectedDevices = false;
			}
			if (hasConnectedDevices) {
				bestNeighborNode = this.bestResource(dtDescriptor, bestNeighborNode, neighborNode, experimentID, request.getDatatype().getId(), sendMetric);
			}
		}
		return bestNeighborNode;
	}

	private ResourcesAvailable getEdgeNodeResourcesAvailable(EdgeNode en, String experimentID, String datatypeID, boolean sendMetric) {
		String monitorURL = this.getUrl("http://", en.getIp(), this.getPorts().getLwcoedge_monitor(), 
				"/monitor/node/resources");
		try {
			ResponseEntity<ResourcesAvailable> httpResp = 
					Util.sendRequest(monitorURL, Util.getDefaultHeaders(), HttpMethod.GET, null, ResourcesAvailable.class);
			if (httpResp.hasBody()) {
				
				if (sendMetric) {
					//data consumed to know whether a node has resources
					try {
						MetricIdentification id = new MetricIdentification(experimentID, "DT_REQSENT_NB_2", null, datatypeID);
						
						// size of request operation 0 bytes
						Long valueOf = 
								Integer.valueOf(0)+
								Util.getObjectSize(httpResp.getHeaders()) +
								Util.getObjectSize(httpResp.getBody());

						registerRequestSizeMetric(id, valueOf);
					} catch (Exception e) {
						this.getLogger().info(e.getMessage());
					}
				}

				return httpResp.getBody();
			}
			throw new Exception( Util.msg("[ERROR] ","No Edge Node resources available! [", en.getHostName(), "]"));
		} catch (Exception e) {
			this.getLogger().info(e.getMessage());
			return null;
		}
	}
	
	private EdgeNode bestResource(Descriptor datatype, EdgeNode bestNeighborNode, EdgeNode neighborNode, String experimentID, String datatypeID, boolean sendMetric) {
		if (bestNeighborNode == null) {
			bestNeighborNode = neighborNode;
		}
		// loading resources "/node/resources"
		ResourcesAvailable bestNeighborNodeRes = this.getEdgeNodeResourcesAvailable(bestNeighborNode, experimentID, datatypeID, sendMetric);
		if (bestNeighborNodeRes == null)
			return null;
		ResourcesAvailable neighborNodeRes = this.getEdgeNodeResourcesAvailable(neighborNode, experimentID, datatypeID, false);
		if (neighborNodeRes == null)
			return null;

		Long needMem = this.neededRes.get(datatype.getType().toString()).getPhysicalMemorySize();
		if (needMem > bestNeighborNodeRes.getFreePhysicalMemorySize() && needMem > neighborNodeRes.getFreePhysicalMemorySize()) {
			return null;
		}
		if (bestNeighborNodeRes.getCpu() > neighborNodeRes.getCpu() && 
				bestNeighborNodeRes.getFreePhysicalMemorySize() > neighborNodeRes.getFreePhysicalMemorySize()) {
			return bestNeighborNode;
		} else {
			if (bestNeighborNodeRes.getFreePhysicalMemorySize() > neighborNodeRes.getFreePhysicalMemorySize()) {
				return bestNeighborNode;
			}
		}
		return neighborNode;
	}

	@Override
	public void registerVNtoDataSharing(VirtualNode newVirtualNode, String... args) throws Exception {
		try {
			if (newVirtualNode.getDatatype().getType() == Type.COMPLEX) {
				this.getLogger().info( "[RegisterVNtoDataSharing] The data type of type Complex is not supported to the collaboration!" );
				return;
			}

			if (!enableCollaboration) {
				this.getLogger().info( "[RegisterVNtoDataSharing] The collaboration process is inactive!" );
				return;
			}

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
				try {
					MetricIdentification id = new MetricIdentification(headers.get("ExperimentID"), "DT_SHARING", null, newVirtualNode.getDatatype().getDescriptorId());
					Long valueOf = Long.parseLong(headers.getOrDefault("RequestSize", "0"));
					registerRequestSizeMetric(id, valueOf);
				} catch (Exception e) {
					this.getLogger().info(e.getMessage());
				}				
			}

			ArrayList<VirtualNode> vnsUpdated = new ArrayList<VirtualNode>();
						
			try {
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
					try {
						MetricIdentification id = new MetricIdentification(headers.get("ExperimentID"), "DT_SHARING", null, newVirtualNode.getDatatype().getDescriptorId());
						Long valueOf = Util.getObjectSize(httpResp.getHeaders()) +
								Util.getObjectSize(httpResp.getBody());
						registerRequestSizeMetric(id, valueOf);
					} catch (Exception e) {
						this.getLogger().info(e.getMessage());
					}				
				}

			} catch (Exception e) {
				this.getLogger().info(Util.msg("[ERROR] ","Registering VN inside of the same node to data sharing ",e.getMessage()));
			}


			//Verifying in the neighboring nodes
			ArrayList<EdgeNode> neighborhood = this.edgeNode.getNeighborhood();
			for (EdgeNode neighborNode : neighborhood) {
				try {
					String neighborVNInstanceCacheUrl =
							this.getUrl("http://", neighborNode.getIp(), this.getPorts().getLwcoedge_vn_instancecache(), 
								"/vninstancecache/cache/list/instances");

					ResponseEntity<VirtualNodeInstances> httpResp =
							Util.sendRequest(neighborVNInstanceCacheUrl, Util.getDefaultHeaders(), HttpMethod.GET, null, VirtualNodeInstances.class);

					if (sendMetric) {
						//DT_SHARING
						//data consumed to received the answer
						try {
							MetricIdentification id = new MetricIdentification(headers.get("ExperimentID"), "DT_SHARING_3", null, newVirtualNode.getDatatype().getDescriptorId());
							Long valueOf = Util.getObjectSize(httpResp.getHeaders()) +
									Util.getObjectSize(httpResp.getBody());
							registerRequestSizeMetric(id, valueOf);
						} catch (Exception e) {
							this.getLogger().info(e.getMessage());
						}				
					}

					ArrayList<VirtualNode> virtualNodeInstances = httpResp.getBody().getVirtualNodeInstances();
					for (VirtualNode neighBorVn : virtualNodeInstances) {
						this.verifyIntersection(newVirtualNode, neighBorVn, vnsUpdated);
					}
					
				} catch (Exception e) {
					this.getLogger().info(Util.msg("[ERROR] ","During the register of the VN  to data sharing in the neighbor node [",neighborNode.getIp(),"] ",e.getMessage()));
				}
			}
			// Re-register the updated Virtual nodes into the Cache
			for (VirtualNode virtualNode : vnsUpdated) {
				this.register(virtualNode);
			}

		} catch (Exception e) {
			this.getLogger().info(Util.msg("[ERROR] ","During the register of the VN  to data sharing ",e.getMessage()));
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
			this.getLogger().info( Util.msg("Updating the Virtual Node instance ", vn.getId(), " into the repository..."));
			String hostName = this.getHostName();
			if (!vn.getHostName().equals(this.getHostName())) {
				hostName = vn.getHostName();
			}
			url = this.getUrl("http://", hostName, this.getPorts().getLwcoedge_vn_instancecache(), "/vninstancecache/register");
			Util.sendRequest(url, Util.getDefaultHeaders(), HttpMethod.POST, vn, Void.class);
			
			this.getLogger().info(Util.msg("Updating the neighbors into the Virtual Node instance ", vn.getId(), "..."));
			//update VN neighboring
			url = this.getUrl("http://", hostName, vn.getPort(), "/vnsensing/neighbor/register");
			Util.sendRequest(url, Util.getDefaultHeaders(), HttpMethod.POST, vn.getNeighbors(), Void.class);		
			
			this.getLogger().info( Util.msg("VN -> ", vn.toString()));
			this.getLogger().info("VN instance updated!");
		} catch (Exception e) {
			this.getLogger().info( Util.msg("[ERROR] ","VN instance not updated into the cache!\n", e.getMessage()));
		}

	}

	/**
	 * This method executes a ping command to get the communication latency time.
	 * @param host The node to perform a ping operation.
	 * @return Communication latency time.
	 */
	private long ping(String host, int times, int packetSize) {
		this.pingRequest.setHost (host);
		this.pingRequest.setPacketSize(packetSize);

		int time = 0;
		// repeat a few times
		for (int count = 0; count < times; count ++) {
			// delegate
			final IcmpPingResponse response = IcmpPingUtil.executePingRequest(this.pingRequest);
			time += response.getRtt();
		}
		Double latency = (double)time/times;
		return latency.longValue() ;
	}

	public void setCollaboration(boolean enable) {
		this.getLogger().info("Configuring the Collaboration process...");
		this.enableCollaboration = enable;
		if (enable) {
			this.getLogger().info("The Collaboration process is enabled!");
		} else {
			this.getLogger().info("The Collaboration process is disabled!");
		}
	}
	
/*
	private static void ping2(String host) {
		final IcmpPingRequest request = IcmpPingUtil.createIcmpPingRequest ();
		request.setHost (host);
		final AsyncCallback<IcmpPingResponse> asyncCallback = new AsyncCallback<IcmpPingResponse> () {
		      public void onSuccess (final IcmpPingResponse response) {
		        // log
		        System.out.println ("response: " + response);
		      }
		      public void onFailure (final Throwable throwable) {
		        // log
		        throwable.printStackTrace ();
		      }
		    };
		    
		IcmpPingUtil.executePingRequest(request, asyncCallback);
	}
*/
/*
	public static void main(String[] args) throws InterruptedException {
		P2PCollaborationService p = new P2PCollaborationService();
		for (int i = 0; i < 4; i++) {
			LocalDateTime start = LocalDateTime.now();
			System.out.println("--------------------");
			System.out.println(start.toString());

			long latency = p.ping("globo.com", 4, 125);
			System.out.println("Ping="+latency);

			LocalDateTime finish = start.plus(latency, ChronoField.MILLI_OF_DAY.getBaseUnit());
			System.out.println(finish.toString());

		}
	}
*/
}