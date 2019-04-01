package br.edu.ufrj.lwcoedge.resourceprovisioner.service;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.ComponentScan;
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
import br.edu.ufrj.lwcoedge.core.service.AsyncService;
import br.edu.ufrj.lwcoedge.core.service.SendMetricService;
import br.edu.ufrj.lwcoedge.core.util.Util;

@Service
@ComponentScan("br.edu.ufrj.lwcoedge.core")
public class ResourceProvisionerService extends AbstractService implements IProvisioning {

	@Autowired
	private AsyncService asyncService;
	
	@Autowired
	SendMetricService metricService;
	
	String vnInstanceCacheUrl, edgeNodeManagerUrl, p2pCollaborationUrl, p2pCollaborationDSUrl, managerApiUrl;

	@Override
	public void appConfig(ApplicationArguments args) throws Exception {
		this.getLogger().info("LW-CoEdge loading application settings...\n");
		if (args != null && !args.getOptionNames().isEmpty()) {
			try {
				this.loadComponentsPort(args);

				this.vnInstanceCacheUrl  = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_vn_instancecache(), "/vninstancecache");
				this.edgeNodeManagerUrl  = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_edgenode_manager(), "/edgenodemanager");
				this.p2pCollaborationUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_p2pcollaboration(), "/p2pcollaboration/sendToNeighborNode");
				this.p2pCollaborationDSUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_p2pcollaboration(), "/p2pcollaboration/registerVNtoDataSharing");
				this.managerApiUrl       = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_manager_api(), "/lwcoedgemgr/metrics/put");
				
				this.getLogger().info(Util.msg("VNInstance cache url = ", this.vnInstanceCacheUrl));
				this.getLogger().info(Util.msg("EdgeNodeManager url = ", this.edgeNodeManagerUrl));
				this.getLogger().info(Util.msg("P2PCollaboration url = ", this.p2pCollaborationUrl));
				this.getLogger().info(Util.msg("P2PCollaboration (DS) url = ", this.p2pCollaborationDSUrl));
				this.getLogger().info(Util.msg("ManagerApi url = ", this.managerApiUrl));

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
				ResponseEntity<VirtualNode> httpRespVN = Util.sendRequest( Util.msg(this.vnInstanceCacheUrl, "/search"),
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
		this.getLogger().info( 
				Util.msg("Starting provisioning process to the Request [", 
						headers.get("RequestID") ,"]...",
						" StartDatetime =", headers.get("StartDateTime")
				) 
		);
		try {
			boolean hasResource = true;
			try {
				ResponseEntity<Boolean> httpRespResource = 
						Util.sendRequest( Util.msg(this.edgeNodeManagerUrl, "/hasResource"), 
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
						httpRespVN = Util.sendRequest( Util.msg(this.edgeNodeManagerUrl, "/containerDeploy"),
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
							Long valueOf = Util.getObjectSize(httpRespVN.getHeaders()) +
									Util.getObjectSize(httpRespVN.getBody());

							metricService.sendMetricSummaryValue(this.managerApiUrl, headers.get("ExperimentID"), "DT_RESP_STARTC", request.getDatatype().getId(), valueOf);
						}

						//Registering the new instance of the VN into the cache
						VirtualNode vn = httpRespVN.getBody();
						this.getLogger().info(Util.msg("Registering Virtual Node instance ", vn.getId(), " into the Repository..."));
						try {
							Util.sendRequest( Util.msg(this.vnInstanceCacheUrl, "/register"), 
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
						asyncService.run(()->{
							try {
								registerToDataSharing(vn, headers);
							} catch (Exception e) {
								String msg = Util.msg("[ERROR] ", e.getMessage());
								this.getLogger().info(msg);
							}
						});

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
						this.getLogger().info("Provisioning (scale-up) process started!");
						ResponseEntity<Boolean> httpResp = 
								Util.sendRequest( Util.msg(this.edgeNodeManagerUrl, "/scaleUp"), 
										Util.getDefaultHeaders(headers), HttpMethod.POST, currentVirtualNode, Boolean.class);

						if (httpResp.hasBody() && httpResp.getBody()) {
							this.getLogger().info("Provisioning (scale-up) process finished!");
							this.getLogger().info("------------------------------");

							return currentVirtualNode;					
						} else {
							String msg = "[WARNING] The VN container cannot be scaled up! Invoking the P2P Collaboration";
							this.getLogger().info(msg);
						}
					} catch (Exception e2) {
						String msg = Util.msg("[ERROR] ","Invoking the Edge Node manager component (scale-up)!\n", e2.getMessage());
						this.getLogger().info(msg);
						//throw new Exception(msg);
					}
				}
			}
			//Collaboration with the neighborhood for finding an Edge Node to meet the request
			asyncService.run(()->{
				try {
					registerToCollaboration(request, headers, sendMetricEnable);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage());
				}
			});
			
		} catch (Exception e) {

			// The request did not submit to collaboration
			this.getLogger().info(e.getMessage());
			if (sendMetricEnable) {
				metricService.sendMetric(this.managerApiUrl, headers.get("ExperimentID"), "REQ_NOT_MET", request.getDatatype().getId());
			}
			try {
				Util.callBack(request.getCallback(), new String("ERROR")); // send an empty answer to the request issuer
			} catch (Exception e1) {
				this.getLogger().info(Util.msg("[ERROR] ","The callBack for the Application failed!\n", e1.getMessage()));
			}

			this.getLogger().info("Provisioning process finished with ERROR!");
			this.getLogger().info("------------------------------");
//			throw e;
		}
		return null;
	}

	private void registerToDataSharing(VirtualNode vn, LinkedHashMap<String, String> headers) throws Exception {
		this.getLogger().info(Util.msg("Registering VN [", vn.getId(), "] to the data sharing."));
		try {
			Util.sendRequest(this.p2pCollaborationDSUrl, 
					Util.getDefaultHeaders(headers), HttpMethod.POST, vn, Void.class);
		} catch (HttpServerErrorException e) {
			String exceptMsg;
			try {
				ResponseBodyException rbe = Util.json2obj(e.getResponseBodyAsString(), ResponseBodyException.class);
				exceptMsg = rbe.getMessage();
			} catch (Exception e1) {
				exceptMsg = e1.getMessage();
			}
			String msg = Util.msg("[ERROR] ", "Invoking the P2P collaboration component (data sharing) to the request [",
					headers.get("RequestID"),
					"]\n",exceptMsg);
			this.getLogger().info(msg);
			throw new Exception (msg);
		}
	}

	private void registerToCollaboration(Request request, LinkedHashMap<String, String> headers, boolean sendMetricEnable) {
		MetricIdentification idOld = new MetricIdentification(headers.get("RequestID"));
		try {
			MetricIdentification id = 
					new MetricIdentification(idOld.getExperiment(), "TIME_SPENT_FW", idOld.getVariation(), request.getDatatype().getId());
			headers.put("RequestID", id.toString()); //a new RequestID is defined before sending the request to collaboration
			headers.put("StartComm", "1"); //Flag to indicate the beginning of P2P Communication
			try {
				this.getLogger().info(
						Util.msg("Invoking P2P collaboration for the request [", headers.get("RequestID"), "]! StartDatetime = ", headers.get("StartDateTime"),"\n")
						);
				Util.sendRequest( this.p2pCollaborationUrl, 
						Util.getDefaultHeaders(headers), HttpMethod.POST, request, Void.class);
			} catch (HttpServerErrorException e) {
				ResponseBodyException rbe = Util.json2obj(e.getResponseBodyAsString(), ResponseBodyException.class);
				throw new Exception(rbe.getMessage());
			}

		} catch (Exception e) {
			String msg = Util.msg("[ERROR] ", "Invoking the P2P collaboration component (sendToNeighborNode)!\n", e.getMessage());
			//throw new Exception (msg);
			this.getLogger().info(msg);
			
			if (sendMetricEnable) {
				metricService.sendMetric(this.managerApiUrl, headers.get("ExperimentID"), "REQ_NOT_MET", request.getDatatype().getId());
			}
			
			try {
				Util.callBack(request.getCallback(), new String("ERROR")); // send an empty answer to the request issuer
			} catch (Exception e1) {
				this.getLogger().info(Util.msg("[ERROR] ","The callBack for the Application failed!\n", e1.getMessage()));
			}

		}
	}
}
