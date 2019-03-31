package br.edu.ufrj.lwcoedge.applicationapi.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import br.edu.ufrj.lwcoedge.core.interfaces.IRest;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricIdentification;
import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.service.AbstractService;
import br.edu.ufrj.lwcoedge.core.service.AsyncService;
import br.edu.ufrj.lwcoedge.core.service.SendMetricService;
import br.edu.ufrj.lwcoedge.core.util.Util;

@Service
@ComponentScan("br.edu.ufrj.lwcoedge.core")
public class ApplicationAPIService extends AbstractService implements IRest {

	private String resourceAllocator, managerApiUrl;
	
	@Autowired
	SendMetricService metricService;
	
	@Autowired
	AsyncService asyncService;
	
	@Override
	public void appConfig(ApplicationArguments args) throws Exception {
		this.getLogger().info("LW-CoEdge loading application settings...\n");
		if (args != null && !args.getOptionNames().isEmpty()) {
			try {
				this.loadComponentsPort(args);
				this.resourceAllocator = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_resourceallocator(), "/resourceallocator/handlerequest");
				this.managerApiUrl = this.getUrl("http://", this.getHostName(), this.getPorts().getLwcoedge_manager_api(), "/lwcoedgemgr/metrics/put");
				this.getLogger().info( Util.msg("ResourceAllocator cache url = ", this.resourceAllocator));
				this.getLogger().info( Util.msg("ManagerApi cache url = ", this.managerApiUrl));
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
	@Async("ProcessExecutor-API")
	public void sendRequest(Request request, String... args) {
		String experimentId = args[0];
		try {
			final String startTime = LocalDateTime.now().toString();
			LinkedHashMap<String, String> headers = new LinkedHashMap<String, String>();			
			MetricIdentification id = new MetricIdentification(experimentId, "TIME_REQ", args[1], request.getDatatype().getId());
			headers.put("RequestID", id.toString()); //RequestID
			headers.put("StartDateTime", startTime); //StartDateTime
			headers.put("ExperimentID", experimentId); //ExperimentID
			String msg = Util.msg("Sending request [", id.toString(), "] to the Resource Allocator! Request time: ", startTime);
			this.getLogger().info(Util.msg(msg));
			Util.sendRequest(this.resourceAllocator, Util.getDefaultHeaders(headers), HttpMethod.POST, request, Void.class);

		} catch (Exception e) {
			metricService.sendMetric(managerApiUrl, experimentId, "REQ_NOT_MET", request.getDatatype().getId());

			asyncService.run(()-> {
				this.callBackResult(request);
			});

			new Exception(Util.msg("The call for the Resource Allocator failed!\n", e.getMessage()));
		}
	}

	private void callBackResult(Request request) {
		this.getLogger().info( Util.msg("Sending Error message to the request issuer...."));
		try {
			Util.callBack(request.getCallback(), new String("ERROR"));
			this.getLogger().info( Util.msg("Data sent to the request issuer."));
		} catch (Exception e) {
			this.getLogger().info( Util.msg("[ERROR] ","The call for the Application failed!\n", e.getMessage()) );
		}

	}

}
