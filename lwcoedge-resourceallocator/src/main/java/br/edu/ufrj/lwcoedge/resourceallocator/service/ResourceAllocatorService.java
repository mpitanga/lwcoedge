package br.edu.ufrj.lwcoedge.resourceallocator.service;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import br.edu.ufrj.lwcoedge.core.interfaces.IRequest;
import br.edu.ufrj.lwcoedge.core.model.Metrics;
import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.model.ResourceProvisioningParams;
import br.edu.ufrj.lwcoedge.core.model.Type;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;
import br.edu.ufrj.lwcoedge.core.service.AbstractService;
import br.edu.ufrj.lwcoedge.core.service.SendMetricService;
import br.edu.ufrj.lwcoedge.core.util.Util;

@Service
@ComponentScan("br.edu.ufrj.lwcoedge.core")
public class ResourceAllocatorService extends AbstractService implements IRequest {

	@Autowired
	SendMetricService metricService;
	
	private String vnInstanceCacheUrl, resourceProvisionerUrl, monitorUrl, managerApiUrl;
	
	@Override
	public void appConfig(ApplicationArguments args) throws Exception {
		this.getLogger().info("LW-CoEdge loading application settings...\n");
		if (args != null && !args.getOptionNames().isEmpty()) {
			try {
				this.loadComponentsPort(args);

				this.vnInstanceCacheUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_vn_instancecache(), "/vninstancecache/search");
				this.resourceProvisionerUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_resourceprovisioner(), "/resourceprovisioner/provisioning");
				this.monitorUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_monitor(), "/monitor/vn/metrics");
				this.managerApiUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_manager_api(), "/lwcoedgemgr/metrics/put");

				this.getLogger().info( Util.msg("VNInstance cache url = ", this.vnInstanceCacheUrl) );
				this.getLogger().info( Util.msg("Resource provisioner url = ",this.resourceProvisionerUrl) );
				this.getLogger().info( Util.msg("Monitor url = ", this.monitorUrl) );

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
	
	@Async("ProcessExecutor-allocator")
	@Override
	public void handleRequest(Request request, String... args) throws Exception {
		LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
		boolean sendMetricEnable = (args.length>0 && !args[2].equals("R"));
		long oldCommLatency = 0;
		if (args.length>0) {
			//args -> RequestID, StartDateTime, ExperimentID, RequestSize, StartComm, CommLatency, TimeSpentWithP2P; total = 7 
			headers.put("RequestID", args[0]);
			headers.put("StartDateTime", args[1]);
			headers.put("ExperimentID", args[2]);
			headers.put("RequestSize", args[3]);
			headers.put("TimeSpentWithP2P", Long.toString(0l));				
			if (args.length>4) {
				headers.put("StartComm", args[4]);
				headers.put("CommLatency", args[5]);
				headers.put("TimeSpentWithP2P", args[6]);
				oldCommLatency += Long.parseLong(args[7]);
			}
			if (args.length==8) {
				this.getLogger().info("Request received from the P2P collaboration!");
				if (sendMetricEnable) {
					Long valueOf = Long.valueOf(headers.get("RequestSize"));
					metricService.sendMetricSummaryValue(this.managerApiUrl, headers.get("ExperimentID"), "DT_REQSENT_NB_0", request.getDatatype().getId(), valueOf);
				}
			} else {
				//(request received).
				this.getLogger().info("Request received from the Application manager (APPMgr)!");
				if (sendMetricEnable) {
					Long valueOf = Long.valueOf(headers.get("RequestSize"));
					metricService.sendMetricSummaryValue(this.managerApiUrl, headers.get("ExperimentID"), "DT_REQREC_APP", request.getDatatype().getId(), valueOf);
				}
			}
		}
		this.getLogger().info( 
			Util.msg("Request [", headers.get("RequestID"),
				" size = [", headers.get("RequestSize"),
				"] submitted to process in ", headers.get("StartDateTime"),
				" - Processing time (with P2P) = ", headers.get("TimeSpentWithP2P"),
				" Old latency = ", Long.toString(oldCommLatency)
			)
		);

		VirtualNode vn = null;
		ResponseEntity<VirtualNode> httpRespVN;
		try {
			httpRespVN = Util.sendRequest(this.vnInstanceCacheUrl, 
					Util.getDefaultHeaders(headers), HttpMethod.POST, request.getDatatype(), VirtualNode.class);
			if (httpRespVN.hasBody()) {
				vn = httpRespVN.getBody();

				if (sendMetricEnable) {
					//data consumed to received the answer
					Long valueOf = Util.getObjectSize(httpRespVN.getHeaders()) +
							Util.getObjectSize(httpRespVN.getBody());
					metricService.sendMetricSummaryValue(this.managerApiUrl, headers.get("ExperimentID"), "DT_RESP_VNCACHE", request.getDatatype().getId(), valueOf);
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
				httpRespVN = Util.sendRequest(this.resourceProvisionerUrl,
						Util.getDefaultHeaders(headers), HttpMethod.POST, paramRP, VirtualNode.class);
				vn = (httpRespVN.hasBody()) ? httpRespVN.getBody() : null;

				if (vn != null && sendMetricEnable) {
					//Bandwidth consumed to received the answer
					Long valueOf = Util.getObjectSize(httpRespVN.getHeaders()) +
							Util.getObjectSize(httpRespVN.getBody());
					metricService.sendMetricSummaryValue(this.managerApiUrl, headers.get("ExperimentID"), "DT_RESP_DEPLOY", request.getDatatype().getId(), valueOf);
				}
			} catch (Exception e) {
				String msg = Util.msg("[ERROR] ","The invocation of the Resource Provisioner generated an error! ", e.getMessage());
				this.getLogger().info(msg);
				throw new Exception(msg);
			}
		} else {
			//this.getLogger().info("Invoking Monitor component to get metrics...");
			// Getting runtime metrics' object from the selected Virtual Node
			Metrics metrics;
			try {
				ResponseEntity<Metrics> httpRespMetrics = 
					Util.sendRequest(this.monitorUrl, Util.getDefaultHeaders(), HttpMethod.POST, vn, Metrics.class);
				if (httpRespMetrics.hasBody()) {
					metrics = httpRespMetrics.getBody();
					
					if (sendMetricEnable) {
						//data consumed to received the answer
						Long valueOf = Util.getObjectSize(httpRespMetrics.getHeaders()) +
								Util.getObjectSize(httpRespMetrics.getBody());
						metricService.sendMetricSummaryValue(this.managerApiUrl, headers.get("ExperimentID"), "DT_RESP_MONITOR", request.getDatatype().getId(), valueOf);
					}
				}
				else
					metrics = new Metrics();
			} catch (Exception e) {
				metrics = new Metrics();
				this.getLogger().info( Util.msg("[ERROR] Accessing the monitor. ", e.getMessage()) );
			}

			if (metrics.isResourceBusy()) {
//				this.getLogger().info( Util.msg("VirtualNodeMetrics : ", metrics.toString()));
				this.getLogger().info( Util.msg("Invoking Resource provisioner to scale-up the VN container[",vn.getId(),"...",
						" Request [", args[0], "] StartDatetime= ", args[1])
				);
				
				// Scale-up the Virtual Node container
				ResourceProvisioningParams paramRP = new ResourceProvisioningParams();
				paramRP.setCurrentVirtualNode(vn);
				paramRP.setRequest(request);
				try {
					httpRespVN = Util.sendRequest(this.resourceProvisionerUrl,
						Util.getDefaultHeaders(headers), HttpMethod.POST, paramRP, VirtualNode.class);
					
					if (!httpRespVN.hasBody()) {
						this.getLogger().info(Util.msg("[No scale-up] The request [",  headers.get("RequestID"), "] was submitted to the collaboration process!"));
						return;
					}
					
					if (sendMetricEnable) {
						//data consumed to received the answer
						Long valueOf = Util.getObjectSize(httpRespVN.getHeaders()) +
								Util.getObjectSize(httpRespVN.getBody());
						metricService.sendMetricSummaryValue(this.managerApiUrl, headers.get("ExperimentID"), "DT_RESP_SCALEUP", request.getDatatype().getId(), valueOf);
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
			metricService.sendMetric(this.managerApiUrl, headers.get("ExperimentID"), "REQ_MET", request.getDatatype().getId());
		}
	}

}
