package br.edu.ufrj.lwcoedge.resourceprovisioner.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import br.edu.ufrj.lwcoedge.core.interfaces.IProvisioning;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricIdentification;
import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.model.ResponseBodyException;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;
import br.edu.ufrj.lwcoedge.core.service.AbstractService;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.core.util.UtilMetric;

@Service
public class ResourceProvisionerService extends AbstractService implements IProvisioning, ApplicationRunner {

	String VNInstanceCacheUrl, EdgeNodeManagerUrl, P2PCollaborationUrl, P2PCollaborationDSUrl, ManagerApiUrl;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (args != null && !args.getOptionNames().isEmpty()) {
			try {
				this.loadComponentsPort(args);

				this.VNInstanceCacheUrl  = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_vn_instancecache(), "/vninstancecache");
				this.EdgeNodeManagerUrl  = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_edgenode_manager(), "/edgenodemanager");
				this.P2PCollaborationUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_p2pcollaboration(), "/p2pcollaboration/sendToNeighborNode");
				this.P2PCollaborationDSUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_p2pcollaboration(), "/p2pcollaboration/registerVNtoDataSharing");
				this.ManagerApiUrl       = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_manager_api(), "/lwcoedgemgr/metrics/put");
				
				this.getLogger().info(Util.msg("VNInstance cache url = ", this.VNInstanceCacheUrl));
				this.getLogger().info(Util.msg("EdgeNodeManager url = ", this.EdgeNodeManagerUrl));
				this.getLogger().info(Util.msg("P2PCollaboration url = ", this.P2PCollaborationUrl));
				this.getLogger().info(Util.msg("P2PCollaboration (DS) url = ", this.P2PCollaborationDSUrl));
				this.getLogger().info(Util.msg("ManagerApi url = ", this.ManagerApiUrl));

			} catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		} else {
			this.getLogger().info("No application settings founded!");
			System.exit(-1);
		}
	}
	
	private void registerRequestSizeMetric(MetricIdentification id, Long valueOf) throws Exception {
		new Thread(()-> {
			//Bandwidth consumed
			try {
				UtilMetric.sendMetricSummaryValue(this.ManagerApiUrl, id.getKey(), id.toString(), valueOf);
			} catch (Exception e) {
				String msg = Util.msg("[ERROR] ","Error submitting the metric [", id.toString(), "] to the registry.", e.getMessage());
				this.getLogger().info(msg);
			}
		}).start();
	}

	@Override
	public synchronized VirtualNode provisioning(VirtualNode currentVirtualNode, Request request, String... args)  throws Exception {
		this.getLogger().info("--------------------------------");
		/*
		 * Due to characteristic multi-thread of this implementation, we re-check if the VN exists into the cache before 
		 * starting the deployment to avoid unneeded provisioning.
		 */
		//args -> requestID, startDateTime, experimentID, requestSize
		LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
		headers.put("RequestID", args[0]);
		headers.put("StartDateTime", args[1]);
		headers.put("ExperimentID", args[2]);
		headers.put("RequestSize", args[3]);

		boolean sendMetricEnable = (args.length>0 && !headers.get("ExperimentID").equals("R"));

		if (currentVirtualNode == null) {
			try {
				this.getLogger().info(
					Util.msg("Checking again if there is a VN instance into the cache to meet the request ->", headers.get("RequestID"))
				);
				ResponseEntity<VirtualNode> httpRespVN = Util.sendRequest( Util.msg(this.VNInstanceCacheUrl, "/search"),
						Util.getDefaultHeaders(headers), HttpMethod.POST, request.getDatatype(), VirtualNode.class);

				if (httpRespVN.hasBody()) {
					this.getLogger().info( "An instance of VN has been found into the cache!" );
					this.getLogger().info("--------------------------------");

					return httpRespVN.getBody();
				}
			} catch (Exception e) {
				String exceptMsg;
				if (e instanceof HttpServerErrorException) {
					ResponseBodyException rbe = Util.json2obj(((HttpServerErrorException)e).getResponseBodyAsString(), ResponseBodyException.class);
					exceptMsg = rbe.getMessage();
				} else {
					exceptMsg = e.getMessage();
				}
				String msg = Util.msg("[ERROR] ", "The invocation of the Virtual Node instance cache generated an error!!\n", exceptMsg);
				this.getLogger().info(msg);
				this.getLogger().info("--------------------------------");
				throw new RuntimeException(msg);
			}
		}
		this.getLogger().info("Starting provisioning process...");
		try {
			boolean hasResource = true;
			try {
				ResponseEntity<Boolean> httpRespResource = 
						Util.sendRequest( Util.msg(this.EdgeNodeManagerUrl, "/hasResource"), 
								Util.getDefaultHeaders(headers), HttpMethod.POST, request.getDatatype(), Boolean.class);				
				hasResource = httpRespResource.getBody();

			} catch (Exception e) {
				String exceptMsg;
				if (e instanceof HttpServerErrorException) {
					ResponseBodyException rbe = Util.json2obj(((HttpServerErrorException)e).getResponseBodyAsString(), ResponseBodyException.class);
					exceptMsg = rbe.getMessage();
				} else {
					exceptMsg = e.getMessage();
				}
				String msg = Util.msg("[ERROR] ", "Invoking the Edge Node manager component (hasResource)!\n", exceptMsg);
				this.getLogger().info(msg);
				this.getLogger().info("--------------------------------");
				throw new Exception(msg);
			}
			if (hasResource) {
				if (currentVirtualNode == null) {
					//Deploy a new Virtual Node container
					ResponseEntity<VirtualNode> httpRespVN;
					try {
						httpRespVN = Util.sendRequest( Util.msg(this.EdgeNodeManagerUrl, "/containerDeploy"),
								Util.getDefaultHeaders(headers), HttpMethod.POST, request.getDatatype(), VirtualNode.class);
					} catch (Exception e) {
						String exceptMsg;
						if (e instanceof HttpServerErrorException) {
							ResponseBodyException rbe = Util.json2obj(((HttpServerErrorException)e).getResponseBodyAsString(), ResponseBodyException.class);
							exceptMsg = rbe.getMessage();
						} else {
							exceptMsg = e.getMessage();
						}
						String msg = Util.msg("[ERROR] ", "Invoking the Edge Node manager component (containerDeploy)!\n", exceptMsg);
						this.getLogger().info(msg);
						throw new Exception(msg);
					}					
					if (httpRespVN.hasBody()) {
						if (sendMetricEnable) {
							//Data consumed to received the answer
							try {
								MetricIdentification id = new MetricIdentification(headers.get("ExperimentID"), "DT_RESP_STARTC", null, request.getDatatype().getId());
								
								Long valueOf = Util.getObjectSize(httpRespVN.getHeaders()) +
										Util.getObjectSize(httpRespVN.getBody());

								registerRequestSizeMetric(id, valueOf);
							} catch (Exception e) {
								this.getLogger().info(e.getMessage());
							}
						}

						//Registering the new instance of the VN into the cache
						VirtualNode vn = httpRespVN.getBody();
						this.getLogger().info(Util.msg("Registering Virtual Node instance ", vn.getId(), " into the Repository..."));
						try {
							Util.sendRequest( Util.msg(this.VNInstanceCacheUrl, "/register"), 
									Util.getDefaultHeaders(), HttpMethod.POST, vn, Void.class);							
						} catch (Exception e) {
							String exceptMsg;
							if (e instanceof HttpServerErrorException) {
								ResponseBodyException rbe = Util.json2obj(((HttpServerErrorException)e).getResponseBodyAsString(), ResponseBodyException.class);
								exceptMsg = rbe.getMessage();
							} else {
								exceptMsg = e.getMessage();
							}
							String msg = Util.msg("[ERROR] ", "VN instance not registered into the cache!\n", exceptMsg);
							this.getLogger().info(msg);
							throw new Exception( msg );
						}
						this.getLogger().info( Util.msg("VN registered -> ", vn.toString()));
							
						//Registering the new VN for data sharing collaboration
						try {
							registerToDataSharing(vn, headers);
						} catch (Exception e) {
							String msg = Util.msg("[ERROR] ", e.getMessage());
							this.getLogger().info(msg);
						}							

						this.getLogger().info("Provisioning process finished!");
						this.getLogger().info("------------------------------");
						return vn;

					} else {
						String msg = "[ERROR] VN Instance not provisioned!";
						this.getLogger().info(msg);
						throw new Exception (msg);
					}
				} else {
					//Reconfigure the Virtual Node container
					try {
						ResponseEntity<Boolean> httpResp = Util.sendRequest(this.EdgeNodeManagerUrl.concat("/scaleUp"), 
								Util.getDefaultHeaders(headers), HttpMethod.POST, currentVirtualNode, Boolean.class);
						if (httpResp.hasBody() && httpResp.getBody()) {
							this.getLogger().info("Provisioning process finished!");
							this.getLogger().info("------------------------------");

							return currentVirtualNode;					
						}
					} catch (Exception e2) {
						String msg = Util.msg("[ERROR] ","Invoking the Edge Node manager component (scale-up)!\n", e2.getMessage());
						this.getLogger().info(msg);
						throw new Exception(msg);
					}
					String msg = "[ERROR] VN container cannot scale-up!";
					this.getLogger().info(msg);
					throw new Exception(msg);						
				}
			}
			//Collaboration with the neighborhood for finding an Edge Node to meet the request
			registerToCollaboration(request, headers);
		} catch (Exception e) {
			
			// The request did not submit to collaboration
			if (args.length > 0) {
				MetricIdentification id = 
						new MetricIdentification(
								new MetricIdentification(args[0]).getExperiment(),
								"REQ_NOT_MET", null, request.getDatatype().getId()
						);
				try {
					UtilMetric.sendMetric(this.ManagerApiUrl, id.getKey(), id.toString(), 1);
				} catch (Exception e1) {
					this.getLogger().info( 
							Util.msg("[ERROR] ","Error submitting the metric [", id.toString(), "] to the registry.", e1.getMessage())
					);
				}
			}

			this.getLogger().info("Provisioning process finished!");
			this.getLogger().info("------------------------------");
			throw e;
		}
		return null;
	}

	private void registerToDataSharing(VirtualNode vn, LinkedHashMap<String, String> headers) throws Exception {
		this.getLogger().info(Util.msg("Registering VN [", vn.getId(), "] to the data sharing."));
		new Thread(()->{
			try {
				Util.sendRequest(this.P2PCollaborationDSUrl, 
						Util.getDefaultHeaders(headers), HttpMethod.POST, vn, Void.class);
			} catch (HttpServerErrorException e) {
				String exceptMsg;
				try {
					ResponseBodyException rbe = Util.json2obj(e.getResponseBodyAsString(), ResponseBodyException.class);
					exceptMsg = rbe.getMessage();
				} catch (Exception e1) {
					exceptMsg = e1.getMessage();
				}
				String msg = Util.msg("[ERROR] ", "Invoking the P2P collaboration component (data sharing)!\n",exceptMsg);
				this.getLogger().info(msg);
				throw new RuntimeException (msg);
			}
		}).start();
	}
	
	private void registerToCollaboration(Request request, LinkedHashMap<String, String> headers) throws Exception {
		new Thread(()->{
			this.getLogger().info(Util.msg("Invoking P2P collaboration for the request [", headers.get("RequestID"), "]!"));
			MetricIdentification idOld = new MetricIdentification(headers.get("RequestID"));
			try {
				MetricIdentification id = 
							new MetricIdentification(idOld.getExperiment(), "TIME_SPENT_FW", idOld.getVariation(), request.getDatatype().getId());
				headers.put("RequestID", id.toString()); //new RequestID to identify the collaboration
				headers.put("StartCommDateTime", LocalDateTime.now().toString()); //Starting Communication date and time
				try {
					Util.sendRequest( this.P2PCollaborationUrl, 
							Util.getDefaultHeaders(headers), HttpMethod.POST, request, Void.class);
				} catch (HttpServerErrorException e) {
					ResponseBodyException rbe = Util.json2obj(e.getResponseBodyAsString(), ResponseBodyException.class);
					throw new Exception(rbe.getMessage());
				}

			} catch (Exception e) {
				String msg = Util.msg("[ERROR] ", "Invoking the P2P collaboration component (sendToNeighborNode)!\n", e.getMessage());
				this.getLogger().info(msg);

				// The request did not submit to collaboration
				MetricIdentification id = 
							new MetricIdentification(
									idOld.getExperiment(),
									"REQ_NOT_MET", null, request.getDatatype().getId()
							);
				try {
					UtilMetric.sendMetric(this.ManagerApiUrl, id.getKey(), id.toString(), 1);
				} catch (Exception e1) {
					this.getLogger().info( 
						Util.msg("[ERROR] ","Error submitting the metric [", id.toString(), "] to the registry.", e1.getMessage())
					);
				}
				throw new RuntimeException (msg);
			}
		}).start();
	}
}
