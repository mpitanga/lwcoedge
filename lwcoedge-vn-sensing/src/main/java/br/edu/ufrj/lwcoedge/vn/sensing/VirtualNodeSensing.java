package br.edu.ufrj.lwcoedge.vn.sensing;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import br.edu.ufrj.lwcoedge.core.interfaces.INeighbor;
import br.edu.ufrj.lwcoedge.core.interfaces.IReceive;
import br.edu.ufrj.lwcoedge.core.interfaces.IRequest;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricIdentification;
import br.edu.ufrj.lwcoedge.core.model.ComponentsPort;
import br.edu.ufrj.lwcoedge.core.model.Data;
import br.edu.ufrj.lwcoedge.core.model.DataSharing;
import br.edu.ufrj.lwcoedge.core.model.DataToShare;
import br.edu.ufrj.lwcoedge.core.model.Element;
import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;
import br.edu.ufrj.lwcoedge.core.model.devices.simulation.EndDevice;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.core.util.UtilMetric;
import br.edu.ufrj.lwcoedge.core.vn.AbstractVirtualNode;

@Service
public class VirtualNodeSensing extends AbstractVirtualNode implements ApplicationRunner, IRequest, INeighbor, IReceive {

	private static final long serialVersionUID = 4464325065673170719L;

	// used by end device tier access simulation
	private ArrayList<EndDevice> endDevices = new ArrayList<EndDevice>();

	private String managerApiUrl, p2pdatasharingURL;
		
	/*
	 * We need to get the parameters when the container is to start.
	 * (non-Javadoc)
	 * @see org.springframework.boot.ApplicationRunner#run(org.springframework.boot.ApplicationArguments)
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
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
		StringBuilder sb = new StringBuilder();
		sb.append("Initializing ").append(this.getName());
		sb.append(" [").append(this.getVn().getId()).append("]");
		sb.append(" [Port=").append(this.getVn().getPort()).append("]...");

		this.getLogger().info("-------------");
		this.getLogger().info(sb.toString());

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
	public void handleRequest(Request request, String... args) throws Exception {
		LocalDateTime ldtRequest;

		//args -> RequestID, StartDateTime, ExperimentID, CommLatency
		LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
		headers.put("RequestID", args[0]);
		headers.put("StartDateTime", args[1]);
		headers.put("ExperimentID", args[2]);
		headers.put("CommLatency", args[3]);

		// if the Proof-of-Concept sends the request, then allow the metric registration.
		final boolean sendMetric = !args[2].equals("R");
		
		this.getLogger().info( "--------------------------------------------------------");
		this.getLogger().info( Util.msg(
				"Virtual Node received a request [", headers.get("RequestID"), 
				"]! Request time: ",headers.get("StartDateTime")));
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
							final MetricIdentification metricID = 
									new MetricIdentification(headers.get("ExperimentID"), metric, null, request.getDatatype().getId());
							try {
								send_Metric(metricID,1);
							} catch (Exception e2) {
								this.getLogger().info( Util.msg("[ERROR] ", e2.getMessage()));
							}
						}
					} else { // requests served from the data cache (M10)
						
						this.getLogger().info("Fresh data obtained from cache!");

						if (sendMetric) {
							final MetricIdentification metricID = 
									new MetricIdentification(headers.get("ExperimentID"), "R_MEM_CACHE", null, request.getDatatype().getId());
							try {
								send_Metric(metricID,1);
							} catch (Exception e2) {
								this.getLogger().info(Util.msg("[ERROR] ", e2.getMessage()));
							}
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
					final MetricIdentification metricID =
							new MetricIdentification(headers.get("ExperimentID"),"R_TEMP_DB", null, request.getDatatype().getId());
					try {
						send_Metric(metricID,1);
					} catch (Exception e2) {
						this.getLogger().info(Util.msg("[ERROR] ", e2.getMessage()));
					}
				}
				for (Data data : value) {
					// get new data from DB
					Data freshData = endDevice.getDataDB();
					data.setValue(freshData.getValue());
					data.setAcquisitiondatetime(freshData.getAcquisitiondatetime());
				}
			}
        }

		new Thread(()->{
			try {
				String jsonData = Util.obj2json(this.getVn().getData());
				this.getLogger().info( Util.msg("Sending data to request issuer...."));
				Util.callBack(request.getCallback(), jsonData);			
			} catch (Exception e) {
				this.getLogger().info( Util.msg("[ERROR] ", "callback URL cannot be executed!\n", e.getMessage()));
			}
		}).start();

		if (sendMetric) {
			
			final LocalDateTime start = LocalDateTime.parse(headers.get("StartDateTime"));
			long latency = 0;
			try {
				latency = Long.parseLong(headers.get("CommLatency"));
			} catch (Exception e2) {
				latency = 0;
			}

			final LocalDateTime finish = LocalDateTime.now();
			final LocalDateTime finishWithLatency = finish.plus(latency, ChronoField.MILLI_OF_DAY.getBaseUnit());
			
			MetricIdentification metricID = new MetricIdentification(headers.get("ExperimentID"),"TIME_REQ", null, request.getDatatype().getId());
			try { // The metric TIME_REQ values (id and start date time) come from the HttpHeader
				send_Metric(metricID, start, finishWithLatency);
			} catch (Exception e2) {
				this.getLogger().info(Util.msg("[ERROR] ","Registering the metric [", metricID.toString(), "]\n", 
						e2.getCause().getMessage()));
			}
			long responseTime = Duration.between(start, finishWithLatency).toMillis();
			this.getLogger().info( Util.msg("Virtual Node process request [", headers.get("RequestID"), "] finished (+latency)! DT=", finishWithLatency.toString(),
					" computational time (ms)->", String.valueOf(responseTime) ));
			
			long responseTimeNoLatency = Duration.between(start, finish).toMillis();
			this.getLogger().info( Util.msg("finished (-latency) DT=", finish.toString()));
			this.getLogger().info( Util.msg("Computational time (ms) (-latency) ->", String.valueOf(responseTimeNoLatency)));
			this.getLogger().info( Util.msg("Latency measured = ", String.valueOf(latency)));

			//checking if the request parameter RTT was fulfilled
			//the unmet RTTs are measured via M11 metric.
			if (request.getParam().getRtt() != null && responseTime > request.getParam().getRtt()) {
				metricID = new MetricIdentification(headers.get("ExperimentID"),"REQ_RTTH_INV", null, request.getDatatype().getId());
				try {
					send_Metric(metricID,1);
				} catch (Exception e2) {
					this.getLogger().info(Util.msg("[ERROR] ", e2.getMessage()));
				}
			}

		} else {
			this.getLogger().info( "Virtual Node process request finished!");
			this.getLogger().info( "--------------------------------------------------------");
		}
		if (sendfreshData) {
			new Thread( ()->{
				shareData(headers);
			}).start();
		}
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

	//Analytic key -> E1-200-M3-104-UFRJ.UbicompLab.temperature
	//Summary key  -> E1.200-M3-0-UFRJ.UbicompLab.temperature
	// The difference between analytic and summary is the variation value.
	private void send_Metric(MetricIdentification metricId, LocalDateTime start, LocalDateTime finish) throws Exception {
		new Thread( ()->{
			this.getLogger().info( Util.msg("Sending metric ", metricId.toString(), " to registry..."));
			try {
				// Analytic UtilMetric.sendMetric(this.managerApiUrl, metricId.getKey(), metricId.toString(), start, finish);
				// Summary
				UtilMetric.sendMetricSummary(this.managerApiUrl, metricId.getKey(), metricId.getSummaryKey(), start, finish);
			} catch (Exception e) {
				this.getLogger().info( 
						Util.msg("[ERROR] ","Submitting the metric [", metricId.toString(), "] to the registry.\n", e.getMessage())
				);
			}			
		}).start();
	}

	@Override
	public void neighborRegister(ArrayList<String> neighbors) {
		this.getLogger().info("[NEIGHBOR REGISTER] Updating the neighborhood list...");
		this.getVn().setNeighbors(neighbors);
		this.getLogger().info( Util.msg("[NEIGHBOR REGISTER] ", this.getVn().getNeighbors().toString()) );
		this.getLogger().info("[NEIGHBOR REGISTER] End Update.");
	}

	@Override
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
