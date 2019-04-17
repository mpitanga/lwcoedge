package br.edu.ufrj.lwcoedge.vn.sensing;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import br.edu.ufrj.lwcoedge.core.model.ComponentsPort;
import br.edu.ufrj.lwcoedge.core.model.Data;
import br.edu.ufrj.lwcoedge.core.model.DataSharing;
import br.edu.ufrj.lwcoedge.core.model.DataToShare;
import br.edu.ufrj.lwcoedge.core.model.Element;
import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;
import br.edu.ufrj.lwcoedge.core.model.devices.simulation.EndDevice;
import br.edu.ufrj.lwcoedge.core.service.AsyncService;
import br.edu.ufrj.lwcoedge.core.service.SendMetricService;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.core.vn.AbstractVirtualNode;

@Service
@ComponentScan("br.edu.ufrj.lwcoedge.core")
public class VirtualNodeSensing extends AbstractVirtualNode implements IVNSensingService {

	private static final long serialVersionUID = 4464325065673170719L;

	@Autowired
	AsyncService asyncService;
	
	@Autowired
	SendMetricService metricService;
	
	// used by end device tier access simulation
	private ArrayList<EndDevice> endDevices = new ArrayList<EndDevice>();

	private String managerApiUrl, p2pdatasharingURL;
		
	/*
	 * We need to get the parameters when the container is to start.
	 */
	@Override
	public void appConfig(ApplicationArguments args) throws Exception {
		// Initializing the Virtual Node object when the service (or container) is started.
		if (args == null || args.getOptionNames().isEmpty()) {
			return;
		}
		ComponentsPort ports = new ComponentsPort();
		
		String jsonVN = args.getOptionValues("VN").get(0);
		Integer port = Integer.valueOf(args.getOptionValues("server.port").get(0));
		Integer lwcoedge_p2pdatasharing = Integer.valueOf(args.getOptionValues("datasharing.port").get(0));
		ports.setLwcoedge_p2pdatasharing(lwcoedge_p2pdatasharing);

		Integer lwcoedge_manager_api = 10500; //default port
		if (args.getOptionNames().contains("managerapi.port")) {
			lwcoedge_manager_api = Integer.valueOf(args.getOptionValues("managerapi.port").get(0));
		}
		ports.setLwcoedge_manager_api(lwcoedge_manager_api);

		VirtualNode virtualNode = Util.json2obj(jsonVN, VirtualNode.class);
		virtualNode.setPort(port);
		
		this.setVn(virtualNode);
		this.setPorts(ports);
		this.start();
	}

	@Override
	public void setPorts(ComponentsPort ports) {
		super.setPorts(ports);
		this.managerApiUrl = 
				this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_manager_api(), "/lwcoedgemgr/metrics/put");
		this.p2pdatasharingURL = 
				this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_p2pdatasharing(), "/p2pdatasharing/sharedata");

	}
	
	@Override
	public void start() {
		this.getLogger().info("-------------");
		this.getLogger().info(Util.msg("Initializing ",this.getName()," [",this.getVn().getId(),"] [Port=",this.getVn().getPort().toString(),"]..."));

		this.getLogger().info("Establishing communication with the end devices...");
		for (Element element : this.getVn().getDatatype().getElement()) {
			EndDevice endDevice = new EndDevice(element.getValue());
			endDevices.add(endDevice);
			this.getLogger().info( Util.msg("End device ->", element.getValue()));
			ArrayList<Data> freshData = new ArrayList<Data>();
			freshData.add(endDevice.getDataDB());
			
			this.getVn().getData().put(element.getValue(), freshData);
		}
		this.setInit(true);
		this.getLogger().info(Util.msg(this.getName(), " is started."));

		this.getLogger().info("-------------");
	}

	@Override
	protected void finalize() throws Throwable {
		this.stop();
	}
	
	@Override
	public void stop() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getName()).append(" [").append(this.getVn().getId()).append("] is stopping...");
		
		this.getLogger().info("-------------");
		this.getLogger().info(sb.toString());
		this.setInit(false);
		this.getLogger().info(Util.msg(getName()," stopped."));
		this.getLogger().info("-------------");
	}

	@Override
	public boolean isRunning() {
		return this.getInit();
	}
	
	@Override
	public VirtualNode getVn() {
		return super.getVn();
	}
	
	@Override
	public void handleRequest(Request request, String... args) throws Exception {
		LocalDateTime ldtRequest;

		//args -> RequestID, StartDateTime, ExperimentID, CommLatency
		LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
		headers.put("RequestID", args[0]);
		headers.put("StartDateTime", args[1]);
		headers.put("ExperimentID", args[2]);
		headers.put("CommLatency", args[3]);
		headers.put("RequestSize", args[4]);
		headers.put("TimeSpentWithP2P", args[5]);
		
		// if the Proof-of-Concept sends the request, then allow the metric registration.
		final boolean sendMetric = !args[2].equals("R");
		
		this.getLogger().info( "--------------------------------------------------------");
		this.getLogger().info( Util.msg(
				"Virtual Node received a request [", headers.get("RequestID"), 
				"]! Request time: ",headers.get("StartDateTime"),
				" TimeSpentWithP2P: ", headers.get("TimeSpentWithP2P")
				)
		);
		ldtRequest  = LocalDateTime.parse(args[1]);

		boolean sendfreshData = false;

		if (this.getVn() == null) {
			this.getLogger().info( "The Virtual Node is not instantiate!");
			throw new Exception("The Virtual Node is not instantiate!");
		}
		
		if ( (request.getParam().getFr() != null) && (!this.getVn().getData().isEmpty()) ) {
			//freshness validation
			// freshness = (request time - acquisition datetime)
			// (request.param.fr ≥freshness)

			for (EndDevice endDevice : endDevices) {
				ArrayList<Data> value = this.getVn().getData().get(endDevice.getHostName());
				for (Data data : value) {
					// Data in memory
					LocalDateTime ldtData = data.getInternalAcquisitiondatetime();
					Duration freshness = Duration.between(ldtData, ldtRequest);

					//if (freshness.toMillis() < endDevice.getInterval()) {
						//Changing the rate to feed the database with sensing data
						//endDevice.setInterval(freshness.toMillis());
					//}
					
					this.getLogger().info( Util.msg("Checking the freshness from the data of the element [",endDevice.getHostName(), "]"));
					if (request.getParam().getFr() < freshness.toMillis()) {

						String msg = "Fresh data obtained from database!";
						this.getLogger().info( "Getting freshdata..." );

						// get new data from internal DB
						String metric = "R_TEMP_DB";
						Data freshData = endDevice.getDataDB();
						ldtData = data.getInternalAcquisitiondatetime();

						// check the freshness with data from the DB
						freshness = Duration.between(ldtData, ldtRequest);
						if (request.getParam().getFr() < freshness.toMillis()) {
							// Direct access
							freshData = endDevice.getData();
							msg = "Fresh data obtained from direct access to the sensor!";
							metric = "R_ENDEV";
						}
						data.setValue(freshData.getValue());
						data.setAcquisitiondatetime(freshData.getAcquisitiondatetime());
						this.getLogger().info( msg );
						
						sendfreshData = true;
						if (sendMetric) {
							metricService.sendMetric(managerApiUrl, headers.get("ExperimentID"), metric, request.getDatatype().getId());
						}
					} else { // requests served from the data cache (M10)
						
						this.getLogger().info("Fresh data obtained from cache!");
						if (sendMetric) {
							metricService.sendMetric(managerApiUrl, headers.get("ExperimentID"), "R_MEM_CACHE", request.getDatatype().getId());
						}
					}
				}
			}
        } else {
			this.getLogger().info("Getting fresh data from temporary Database because no freshness property has been configured....");
        	// to get fresh data
        	sendfreshData = true;
        	//dataToSend = this.getVn().getData();
			for (EndDevice endDevice : endDevices) {
				ArrayList<Data> value = this.getVn().getData().get(endDevice.getHostName());
				if (sendMetric) {
					metricService.sendMetric(managerApiUrl, headers.get("ExperimentID"), "R_TEMP_DB", request.getDatatype().getId());
				}
				for (Data data : value) {
					// get new data from DB
					Data freshData = endDevice.getDataDB();
					data.setValue(freshData.getValue());
					data.setAcquisitiondatetime(freshData.getAcquisitiondatetime());
				}
			}
        }

		// Callback
		// Sending data to request issuer.
		asyncService.run(()-> {
			this.callBackResult(request, headers.get("RequestID"));
		});

		if (sendMetric) {
			asyncService.run(()-> {
				sendAllMetrics(request, headers);
			});
		}		
		if (sendfreshData) {
			asyncService.run(()-> {
				shareData(headers);
			});
		}
		this.getLogger().info( "Virtual Node process request finished!");
		this.getLogger().info( "--------------------------------------------------------");
	}

	private void sendAllMetrics(Request request, LinkedHashMap<String, String> headers) {
		this.getLogger().info( Util.msg("Registering metrics of the request [",headers.get("RequestID"),"]..." ));
		final LocalDateTime start = LocalDateTime.parse(headers.get("StartDateTime"));
		long commlatency = 0;
		try {
			commlatency = Long.parseLong(headers.get("CommLatency"));
		} catch (Exception e2) {
			commlatency = 0;
		}

		long timeSpentWithP2P = 0;
		try {
			timeSpentWithP2P = Long.parseLong(headers.get("TimeSpentWithP2P"));
		} catch (Exception e2) {
			timeSpentWithP2P = 0;
		}

		final LocalDateTime finish = LocalDateTime.now();
		
		// The metric TIME_REQ values (id and start date time) come from the HttpHeader
		metricService.sendMetricSummary(managerApiUrl, headers.get("ExperimentID"),"TIME_REQ", request.getDatatype().getId(), start, finish);

		long responseTime = Duration.between(start, finish).toMillis();
		long totalResponseTime = responseTime+timeSpentWithP2P+commlatency;

		this.getLogger().info( Util.msg("Virtual Node process request [", headers.get("RequestID"), "] finished! "));
		this.getLogger().info( Util.msg("(a) Computational time (ms)->", String.valueOf(responseTime)));
		this.getLogger().info( Util.msg("(b) Latency measured = ", String.valueOf(commlatency)));
		this.getLogger().info( Util.msg("(c) Time of P2P = ", String.valueOf(timeSpentWithP2P)));
		this.getLogger().info( Util.msg("Total response time (a+b+c)= ", String.valueOf(totalResponseTime)));

		//checking if the request parameter RTT was fulfilled
		//the unmet RTTs are measured via M11 metric.
		if (request.getParam().getRtt() != null && totalResponseTime > request.getParam().getRtt()) {
			metricService.sendMetric(managerApiUrl, headers.get("ExperimentID"),"REQ_RTTH_INV", request.getDatatype().getId());
		}
		
		if (timeSpentWithP2P>0) {
			metricService.sendMetricSummaryValue(managerApiUrl, headers.get("ExperimentID"),"TIME_SPENT_P2P", request.getDatatype().getId(), timeSpentWithP2P);
		}

		if (commlatency>0) {
			metricService.sendMetricSummaryValue(managerApiUrl, headers.get("ExperimentID"),"COMM_LATENCY", request.getDatatype().getId(), commlatency);
		}

		metricService.sendMetricSummaryValue(managerApiUrl, headers.get("ExperimentID"),"TOT_RESPONSE_TIME", request.getDatatype().getId(), totalResponseTime);

	}
	
	private void callBackResult(Request request, String requestId) {
		StopWatch callBackTime = new StopWatch();
		callBackTime.start();

		this.getLogger().info( Util.msg("Sending data to the request issuer [", requestId,"]...."));
		try {
			String jsonData = Util.obj2json(this.getVn().getData());
			Util.callBack(request.getCallback(), jsonData);			
			this.getLogger().info( Util.msg("Data sent to the request issuer."));
		} catch (Exception e) {
			this.getLogger().info( Util.msg("[ERROR] ", "callback URL cannot be executed!\n", e.getMessage()));
		}

		callBackTime.stop();
		this.getLogger().info( Util.msg("CallBack response time due to latency ->", Long.toString(callBackTime.getTotalTimeMillis())));

	}

	@Override
	@Async("ProcessExecutor-VnSensing")
	public void neighborRegister(ArrayList<String> neighbors) {
		this.getLogger().info("[NEIGHBOR REGISTER] Updating the neighborhood list...");
		this.getVn().setNeighbors(neighbors);
		this.getLogger().info( Util.msg("[NEIGHBOR REGISTER] ", this.getVn().getNeighbors().toString()) );
		this.getLogger().info("[NEIGHBOR REGISTER] End Update.");
	}

	@Override
	@Async("ProcessExecutor-VnSensing")
	public void setData(String element, ArrayList<Data> data, String... args) {
		this.getLogger().info( Util.msg("[SETDATA] Request ID [", args[0], "]..."));		
		this.getLogger().info( Util.msg("[SETDATA] Updating data for the element [", element, "]..."));
		ArrayList<Data> currentData = this.getVn().getData(element);
		if (currentData != null && currentData.containsAll(data)) {
			this.getLogger().info("[SETDATA] No data update necessary!");
			return;
		}
		this.getVn().setData(element, data);
		this.getLogger().info( Util.msg("[SETDATA] ", this.getVn().getData(element).toString()) );
		this.getLogger().info("[SETDATA] End Update.");

//		shareData(args);
	}

	private void shareData(LinkedHashMap<String, String> headers) {
		// call the data sharing to replicate the fresh data
		this.getLogger().info("[SHARE DATA] Invoking the P2P data sharing...");		
		this.getLogger().info("[SHARE DATA] Verifying the neighbors to the P2P data sharing...");
		if (this.getVn().getNeighbors().isEmpty()) {
			this.getLogger().info("[SHARE DATA] There is no neighboring virtual node available for P2P data sharing!");
			this.getLogger().info("[SHARE DATA] End invoking P2P data sharing.");
			return;
		}
		this.getLogger().info("[SHARE DATA] Preparing the request to the P2P data sharing...");
		// The Virtual Node instance is cloned to send only the primary attributes.
		VirtualNode virtualNode = new VirtualNode();
		virtualNode.setId(this.getVn().getId());
//		virtualNode.setDatatype(this.getVn().getDatatype());
		virtualNode.setNeighbors(this.getVn().getNeighbors());
		virtualNode.setPort(this.getVn().getPort());

		for (EndDevice endDevice : this.endDevices) {
			String element = endDevice.getHostName();
			ArrayList<Data> data = this.getVn().getData().get(element);

			DataToShare ds = new DataToShare();
			ds.setElement(element);
			ds.setData(data);
			
			DataSharing dataSharing = new DataSharing();
			dataSharing.setVirtualNode(virtualNode);
			dataSharing.setDs(ds);

			this.getLogger().info(Util.msg("[SHARE DATA] Sending the request to the P2P data sharing..."));
			this.getLogger().info(Util.msg("[SHARE DATA] ", dataSharing.toString()));
			try {
				Util.sendRequest(this.p2pdatasharingURL, Util.getDefaultHeaders(headers), HttpMethod.POST, dataSharing, Void.class);
			} catch (Exception e) {
				this.getLogger().info(Util.msg("[ERROR] ", e.getMessage()));
			}			
		}
		this.getLogger().info("[SHARE DATA] End invoking P2P data sharing.");
	}

}
