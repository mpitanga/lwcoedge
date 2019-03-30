package br.edu.ufrj.lwcoedge.applicationapi.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import br.edu.ufrj.lwcoedge.core.interfaces.IRest;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricIdentification;
import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.service.AbstractService;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.core.util.UtilMetric;

@Service
public class ApplicationAPIService extends AbstractService implements IRest {

	private String ResourceAllocator, ManagerApiUrl;
	
	@Override
	public void appConfig(ApplicationArguments args) throws Exception {
		this.getLogger().info("LW-CoEdge loading application settings...\n");
		if (args != null && !args.getOptionNames().isEmpty()) {
			try {
				this.loadComponentsPort(args);
				this.ResourceAllocator = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_resourceallocator(), "/resourceallocator/handlerequest");
				this.ManagerApiUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_manager_api(), "/lwcoedgemgr/metrics/put");
				this.getLogger().info( Util.msg("ResourceAllocator cache url = ", this.ResourceAllocator));
				this.getLogger().info( Util.msg("ManagerApi cache url = ", this.ManagerApiUrl));
			} catch (Exception e) {
				this.getLogger().info(e.getMessage());
				System.exit(0);
			}
		} else {
			this.getLogger().info("No application settings founded!");
			System.exit(0);
		}
		this.getLogger().info("");
		this.getLogger().info("LW-CoEdge application settings loaded.\n");
	}

	@Override
	@Async("threadPoolTaskExecutor-API")
	public void sendRequest(Request request, String... args) {
		try {
			final String startTime = LocalDateTime.now().toString();
			LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();			
			MetricIdentification id = new MetricIdentification(args[0], "TIME_REQ", args[1], request.getDatatype().getId());
			headers.put("RequestID", id.toString()); //RequestID
			headers.put("StartDateTime", startTime); //StartDateTime
			headers.put("ExperimentID", args[0]); //ExperimentID
			String msg = Util.msg("Sending request [", id.toString(), "] to the Resource Allocator! Request time: ", startTime);
			this.getLogger().info(Util.msg(msg));
			Util.sendRequest(this.ResourceAllocator, Util.getDefaultHeaders(headers), HttpMethod.POST, request, Void.class);

		} catch (Exception e) {

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

			try {
				Util.callBack(request.getCallback(), new String("ERROR")); // send an empty answer to the request issuer
			} catch (Exception e1) {
				new Exception(Util.msg("[ERROR] ","The call for the Application failed!\n", e1.getMessage()));
			}
			new Exception(Util.msg("The call for the Resource Allocator failed!\n", e.getMessage()));
		}
	}

}
