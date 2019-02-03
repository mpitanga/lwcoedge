package br.edu.ufrj.lwcoedge.p2pdatasharing.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.edu.ufrj.lwcoedge.core.interfaces.IShare;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricIdentification;
import br.edu.ufrj.lwcoedge.core.model.Data;
import br.edu.ufrj.lwcoedge.core.model.DataToShare;
import br.edu.ufrj.lwcoedge.core.model.Datatype;
import br.edu.ufrj.lwcoedge.core.model.Element;
import br.edu.ufrj.lwcoedge.core.model.Type;
import br.edu.ufrj.lwcoedge.core.model.VirtualNode;
import br.edu.ufrj.lwcoedge.core.service.AbstractService;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.core.util.UtilMetric;

@Service
public class P2PDataSharingService extends AbstractService implements ApplicationRunner, IShare {

	private boolean enableShareData = true;
	private String ManagerApiUrl;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (args != null && !args.getOptionNames().isEmpty()) {
			try {
				this.loadComponentsPort(args);
				this.ManagerApiUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_manager_api(), "/lwcoedgemgr/metrics/put");
			} catch (Exception e) {
				this.getLogger().info(e.getMessage());
				System.exit(-1);
			}
		} else {
			this.getLogger().info("No application settings founded!");
			System.exit(-1);
		}				
	}

	private VirtualNode getNeighborVirtualNode(String vnID) throws Exception {
		//MPITANGADELL.UFRJ.UbicompLab.temperature
		String[] idElements = vnID.split("\\.");
		String host = idElements[0];
		Datatype datatype = new Datatype();
		datatype.setId(vnID.substring(host.length()+1));
		String url = this.getUrl("http://", host, this.getPorts().getLwcoedge_vn_instancecache(), "/vninstancecache/search");
		
		ResponseEntity<VirtualNode> httpRespVN = 
				Util.sendRequest(url, Util.getDefaultHeaders(), HttpMethod.POST, datatype, VirtualNode.class);

		if (httpRespVN.hasBody()) {
			return httpRespVN.getBody();
		}

		return null;
	}
	
	@Override
	public void shareData(VirtualNode virtualNode, String element, ArrayList<Data> data, String... args) throws Exception {
		if (!enableShareData) {
			this.getLogger().info( "The data sharing process is inactive!" );
			return;
		}

		LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();
		headers.put("RequestID", args[0]);
		headers.put("StartDateTime", args[1]);
		headers.put("ExperimentID", args[2]);

		List<String> neighbors = virtualNode.getNeighbors();
		String url, app;
		this.getLogger().info( Util.msg("Starting the data sharing from the Virtual Node [",virtualNode.getId(), "]") );
		for (String neighborVNID : neighbors) {
			// avoiding cross-reference between virtual nodes
			if (neighborVNID.equals(virtualNode.getId()))
				continue;

			try {
				VirtualNode neighborVN = this.getNeighborVirtualNode(neighborVNID);
				if (neighborVN == null)
					continue;

				boolean exists = neighborVN.getDatatype().getElement().contains(new Element(element));
				if (!exists)
					continue;

				this.getLogger().info(Util.msg("Sending fresh data to the neighbor Virtual Node [",neighborVN.getId(), "] ..."));
				DataToShare dataToshare = new DataToShare(element, data);
				app = (neighborVN.getDatatype().getType() == Type.SIMPLE) ? "/vnsensing/set/data" : "/vnactuation/set/data";
				url = this.getUrl("http://", neighborVN.getHostName(), neighborVN.getPort(), app);
				
				Util.sendRequest(url, Util.getDefaultHeaders(headers), HttpMethod.POST, dataToshare, Void.class);
				this.getLogger().info("Fresh data sent to the neighbor Virtual Node with success!");

				try { //M12 -> data interchange using data sharing
					MetricIdentification id = new MetricIdentification(headers.get("ExperimentID"), "AVG_DTSHARING", null, neighborVN.getDatatype().getDescriptorId());
					Long valueOf = Long.valueOf(args[3]);
					registerRequestSizeMetric(id, valueOf);
				} catch (Exception e) {
					this.getLogger().info( Util.msg("[ERROR shareData]", e.getCause().getMessage()));
				}

			} catch (Exception e) {
				String msg = Util.msg("[ERROR shareData] ","Sending the fresh data to the Virtual Node failed!\n",e.getMessage());
				this.getLogger().info(msg);
			}
		}
		this.getLogger().info("Data sharing finished!");
	}

	public void setDataSharing(boolean enable) {
		this.getLogger().info("Configuring the data sharing process...");
		this.enableShareData = enable;
		if (enable) {
			this.getLogger().info("The data sharing process is enabled!");
		} else {
			this.getLogger().info("The data sharing process is disabled!");
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

}
