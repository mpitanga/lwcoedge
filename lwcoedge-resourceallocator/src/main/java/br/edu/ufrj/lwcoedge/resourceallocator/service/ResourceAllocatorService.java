package br.edu.ufrj.lwcoedge.resourceallocator.service;

import java.util.LinkedHashMap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.edu.ufrj.lwcoedge.core.interfaces.IRequest;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricIdentification;
import br.edu.ufrj.lwcoedge.core.model.Metrics;
import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.model.ResourceProvisioningParams;
import br.edu.ufrj.lwcoedge.core.model.Type;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;
import br.edu.ufrj.lwcoedge.core.service.AbstractService;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.core.util.UtilMetric;

@Service
public class ResourceAllocatorService extends AbstractService implements ApplicationRunner, IRequest {

	private String VNInstanceCacheUrl, ResourceProvisionerUrl, MonitorUrl, ManagerApiUrl;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (args != null && !args.getOptionNames().isEmpty()) {
			try {
				this.loadComponentsPort(args);

				this.VNInstanceCacheUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_vn_instancecache(), "/vninstancecache/search");
				this.ResourceProvisionerUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_resourceprovisioner(), "/resourceprovisioner/provisioning");
				this.MonitorUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_monitor(), "/monitor/vn/metrics");
				this.ManagerApiUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_manager_api(), "/lwcoedgemgr/metrics/put");

				this.getLogger().info( Util.msg("VNInstance cache url = ", this.VNInstanceCacheUrl) );
				this.getLogger().info( Util.msg("Resource provisioner url = ",this.ResourceProvisionerUrl) );
				this.getLogger().info( Util.msg("Monitor url = ", this.MonitorUrl) );

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
	public void handleRequest(Request request, String... args) throws Exception {
		LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
		boolean sendMetricEnable = (args.length>0 && !args[2].equals("R"));
		if (args.length>0) {
			//args -> RequestID, StartDateTime, ExperimentID, RequestSize, StartCommDateTime, StartP2PDateTime, CommLatency; total = 7 
			headers.put("RequestID", args[0]);
			headers.put("StartDateTime", args[1]);
			headers.put("ExperimentID", args[2]);
			headers.put("RequestSize", args[3]);
			if (args.length>4) {
				headers.put("StartCommDateTime", args[4]);
				headers.put("StartP2PDateTime", args[5]);
				headers.put("CommLatency", args[6]);
			}
			if (args.length==7) {
				this.getLogger().info("Request received from the P2P collaboration!");
				this.getLogger().info( Util.msg("Request ", headers.get("RequestID")," size = ", headers.get("RequestSize")) );
				if (sendMetricEnable) {
					try {
						MetricIdentification id = new MetricIdentification(headers.get("ExperimentID"), "DT_REQSENT_NB_0", null, request.getDatatype().getId());
						Long valueOf = Long.valueOf(headers.get("RequestSize"));
						registerRequestSizeMetric(id, valueOf);
					} catch (Exception e) {
						this.getLogger().info(e.getMessage());
					}					
				}

			} else {
				//(request received).
				if (sendMetricEnable) {
					try {
						MetricIdentification id = new MetricIdentification(headers.get("ExperimentID"), "DT_REQREC_APP", null, request.getDatatype().getId());
						Long valueOf = Long.valueOf(headers.get("RequestSize"));
						registerRequestSizeMetric(id, valueOf);
					} catch (Exception e) {
						this.getLogger().info(e.getMessage());
					}					
				}
			}
			this.getLogger().info( Util.msg("Request [", headers.get("RequestID"), "] submitted to process in ", headers.get("StartDateTime")));
		}
		VirtualNode vn = null;
		ResponseEntity<VirtualNode> httpRespVN;
		try {
			httpRespVN = Util.sendRequest(this.VNInstanceCacheUrl, 
					Util.getDefaultHeaders(headers), HttpMethod.POST, request.getDatatype(), VirtualNode.class);
			if (httpRespVN.hasBody()) {
				vn = httpRespVN.getBody();

				if (sendMetricEnable) {
					//data consumed to received the answer
					try {
						MetricIdentification id = new MetricIdentification(headers.get("ExperimentID"), "DT_RESP_VNCACHE", null, request.getDatatype().getId());
						Long valueOf = Util.getObjectSize(httpRespVN.getHeaders()) +
								Util.getObjectSize(httpRespVN.getBody());
						registerRequestSizeMetric(id, valueOf);
					} catch (Exception e) {
						this.getLogger().info(e.getMessage());
					}					
				}

			}
		} catch (Exception e1) {
			String msg = Util.msg("[ERROR] ", "The invocation of the Virtual Node instance cache generated an error!!\n", e1.getMessage());
			this.getLogger().info(msg);
			throw new RuntimeException(msg);
		}
		if (vn == null) { // A new Virtual Node
			this.getLogger().info("Invoking Resource provisioner [Deploy]...");
			ResourceProvisioningParams paramRP = new ResourceProvisioningParams();
			paramRP.setCurrentVirtualNode(null);
			paramRP.setRequest(request);
			try {
				httpRespVN = Util.sendRequest(this.ResourceProvisionerUrl,
						Util.getDefaultHeaders(headers), HttpMethod.POST, paramRP, VirtualNode.class);
				vn = (httpRespVN.hasBody()) ? httpRespVN.getBody() : null;

				if (vn != null && sendMetricEnable) {
					//Bandwidth consumed to received the answer
					try {
						MetricIdentification id = new MetricIdentification(headers.get("ExperimentID"), "DT_RESP_DEPLOY", null, request.getDatatype().getId());
						Long valueOf = Util.getObjectSize(httpRespVN.getHeaders()) +
								Util.getObjectSize(httpRespVN.getBody());
						registerRequestSizeMetric(id, valueOf);
					} catch (Exception e) {
						this.getLogger().info(e.getMessage());
					}					
				}
			} catch (Exception e) {
				String msg = Util.msg("[ERROR] ","The invocation of the Resource Provisioner generated an error!\n", e.getMessage());
				this.getLogger().info(msg);
				throw new Exception(msg);
			}
		} else {
			this.getLogger().info("Invoking Monitor component to get metrics...");
			// Getting runtime metrics' object from the selected Virtual Node
			Metrics metrics;
			try {
				ResponseEntity<Metrics> httpRespMetrics = 
					Util.sendRequest(this.MonitorUrl, Util.getDefaultHeaders(), HttpMethod.POST, vn, Metrics.class);
				if (httpRespMetrics.hasBody()) {
					metrics = httpRespMetrics.getBody();
					
					if (sendMetricEnable) {
						//data consumed to received the answer
						try {
							MetricIdentification id = new MetricIdentification(headers.get("ExperimentID"), "DT_RESP_MONITOR", null, request.getDatatype().getId());
							Long valueOf = Util.getObjectSize(httpRespMetrics.getHeaders()) +
									Util.getObjectSize(httpRespMetrics.getBody());
							registerRequestSizeMetric(id, valueOf);
						} catch (Exception e) {
							this.getLogger().info(e.getMessage());
						}					
					}

				}
				else
					metrics = new Metrics();
			} catch (Exception e) {
				metrics = new Metrics();
			}
			if (metrics.isResourceBusy()) {
				this.getLogger().info("Invoking Resource provisioner to scale-up the VN container...");
				// Scale-up the Virtual Node container
				ResourceProvisioningParams paramRP = new ResourceProvisioningParams();
				paramRP.setCurrentVirtualNode(vn);
				paramRP.setRequest(request);
				try {
					httpRespVN = Util.sendRequest(this.ResourceProvisionerUrl,
						Util.getDefaultHeaders(headers), HttpMethod.POST, paramRP, VirtualNode.class);
					
					if (sendMetricEnable) {
						//data consumed to received the answer
						try {
							MetricIdentification id = new MetricIdentification(headers.get("ExperimentID"), "DT_RESP_SCALEUP", null, request.getDatatype().getId());
							Long valueOf = Util.getObjectSize(httpRespVN.getHeaders()) +
									Util.getObjectSize(httpRespVN.getBody());
							registerRequestSizeMetric(id, valueOf);
						} catch (Exception e) {
							this.getLogger().info(e.getMessage());
						}					
					}

				} catch (Exception e) {
					String msg = Util.msg("[ERROR] ","The invocation of the Resource Provisioner generated an error!\n", e.getMessage());
					this.getLogger().info(msg);
					throw new RuntimeException(msg);
				}
			}
		}
		if (vn != null) {
			try {
				this.sendRequestToVirtualNode(vn, request, headers);
			} catch (Exception e) {
				this.getLogger().info(Util.msg("[ERROR] ",e.getMessage()));
				throw new RuntimeException(e.getMessage());
			}
		} else {
			this.getLogger().info("The request was submitted to the collaboration process!");
		}
	}
	
	private void sendRequestToVirtualNode(VirtualNode vn, Request request, LinkedHashMap<String, String> headers) throws Exception {
		final String req = (headers.size()>0) ? headers.get("RequestID") : request.getDatatype().getId();
		try {
			StringBuilder url = new StringBuilder();
			try {
				String vn_app = "/vnsensing";
				if (vn.getDatatype().getType() == Type.ACTUATION) {
					vn_app = "/vnactuation";
				} else if (vn.getDatatype().getType() == Type.COMPLEX) {
					vn_app = "/vndatahandling";			
				}
				url.append("http://").append(vn.getHostName()).append(":").append(vn.getPort()).append(vn_app).append("/app_request");
				this.getLogger().info( Util.msg("Forwarding the request ", req, " to the VN :", url.toString()) );
			} catch (Exception e) {
				String msg = Util.msg("[ERROR] ","Invalid URL ", e.getMessage());
				this.getLogger().info( msg );
				throw new RuntimeException( msg );
			}
			Util.sendRequest(url.toString(), Util.getDefaultHeaders(headers), HttpMethod.POST, request, Void.class);
		} catch (Exception e) {
			String msg = Util.msg("[ERROR] ", "Error forwarding the request ", req," to the Virtual Node [",
					vn.getId(), "] ", e.getMessage());
			throw new RuntimeException( msg );
		}

		if (headers.size() > 0) {
			MetricIdentification id = new MetricIdentification(headers.get("ExperimentID"), "REQ_MET", null, request.getDatatype().getId());
			try {
				UtilMetric.sendMetric(this.ManagerApiUrl, id.getKey(), id.toString(), 1);
			} catch (Exception e) {
				String msg = Util.msg("[ERROR] ","Error submitting the metric [", id.toString(), "] to the registry.", e.getMessage());
				this.getLogger().info(msg);
			}
		}
	}
}
