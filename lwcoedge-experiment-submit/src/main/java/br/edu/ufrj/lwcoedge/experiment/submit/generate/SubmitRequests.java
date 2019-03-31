package br.edu.ufrj.lwcoedge.experiment.submit.generate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ufrj.lwcoedge.core.service.AbstractService;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.experiment.core.model.ExperimentSubmitConfig;
import br.edu.ufrj.lwcoedge.experiment.submit.Service.ServiceDB;
import br.edu.ufrj.lwcoedge.experiment.submit.db.entities.Requests;

@Component
public class SubmitRequests extends AbstractService implements ApplicationRunner {

	@Autowired
	ServiceDB service;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		this.getLogger().info("Loading the LW-CoEdge Experiment-Submit settings...");
		String fileName = null;
		try {
			fileName = args.getOptionValues("experiment-submit-config").get(0);
		} catch (Exception e) {
			this.getLogger().info( "\n\n[ERROR] The parameter [experiment-submit-config] was not configured!\n" );
			System.exit(-1);
		}
		ObjectMapper objectMapper = new ObjectMapper();
		ExperimentSubmitConfig config = objectMapper.readValue(new File(fileName), ExperimentSubmitConfig.class);
		this.getLogger().info("LW-CoEdge Experiment-Submit settings loaded.");
		this.getLogger().info(Util.msg("Configuration ->",config.toString()));

		try {
			this.loadComponentsPort(args);			
		} catch (Exception e) {
			this.getLogger().info( "\n\n[ERROR] The parameter [PortsConfig] was not configured!\n" );
			System.exit(-1);
		}
		
		String experimentName = config.getExperimentname();
		String basePath = config.getBasepath();
		String path = basePath+experimentName+"/";
		String[] edgeNodes = config.getEdgenodes();

		String urlCallBack = "http://192.168.237.127:8081/myapp/callback/result";
		
		boolean executeExperiment = config.isExecuteexperiment();
		boolean clearMetrics = config.isClearmetrics();
		boolean generateResults = config.isGenerateresults();

		clearCacheMetrics(edgeNodes, "E0", -1);

		runExperiment(experimentName, path, edgeNodes, executeExperiment, clearMetrics, generateResults, urlCallBack);

	}

	private void runExperiment(String experimentName, String path, String[] edgeNodes, 
			boolean executeExperiment, boolean clearMetrics, boolean generateResults, String urlCallBack) {

		if (!executeExperiment) {
			this.getLogger().info("The execution of the Experiment is disabled!");
			return;
		}
		Boolean oldActivatecollaboration = null;
		Boolean oldActivateDataSharing = null;
		String oldExperimentCode = null;
		
		int oldIdxhost = -1;
		
		LocalDateTime startEx = LocalDateTime.now();
		int timeSleep = 100;
		//int firstRequests = 0;
		try {
			List<Requests> requests = service.getRequests(experimentName);
			for (Requests request : requests) {

				if (oldExperimentCode == null || !oldExperimentCode.equals(request.getExperimentcode())) {
										
					if (clearMetrics) {
						
						if (oldExperimentCode  != null) {
							if (generateResults) {
								generateFileResults(path, edgeNodes, -1 /*oldIdxhost*/);
							}
						}
						//clearCacheMetrics(edgeNodes, request.getExperimentcode(), request.getIdxhost());
						clearCacheMetrics(edgeNodes, request.getExperimentcode(), -1);
					}
					oldExperimentCode = request.getExperimentcode();
					oldIdxhost = request.getIdxhost();
				}

				if (oldActivatecollaboration == null || oldActivatecollaboration != request.isActivatecollaboration()) {
					oldActivatecollaboration = request.isActivatecollaboration();
					activateCollaboration(edgeNodes, request.isActivatecollaboration(), request.getIdxhost());					
				} 
				if (oldActivateDataSharing == null || oldActivateDataSharing != request.isActivatedatasharing()) {
					oldActivateDataSharing = request.isActivatedatasharing();
					if (request.isActivatecollaboration()) {
						activateDataSharing(edgeNodes, request.isActivatedatasharing(), request.getIdxhost());
					} else {
						activateDataSharing(edgeNodes, false, request.getIdxhost());
					}
				}
				sendRequest(request, urlCallBack);
/*
				if (++firstRequests > 100) {
					if (timeSleep == 0) {
						timeSleep = 2000; // wait 2s to dispatch the firsts requests
					} else {
						timeSleep = 100;
					}
				} else {
					timeSleep = 50;
				}
*/				
				Thread.sleep(timeSleep);
			}
			if (oldExperimentCode  != null) {
				if (generateResults) {
					generateFileResults(path, edgeNodes, -1 /*oldIdxhost*/);
				}
			}
		} catch (Exception e) {
			this.getLogger().info("[ERROR] "+e.getMessage());
		}

		LocalDateTime finishEx = LocalDateTime.now();
		Duration d = Duration.between(startEx, finishEx);
		this.getLogger().info("----------------------------------");
		this.getLogger().info("Experiment Name    -> "+experimentName);
		this.getLogger().info("Start experiment   -> "+startEx);
		this.getLogger().info("Finish experiment  -> "+finishEx);
		this.getLogger().info("Time elapsed (sec) -> "+d.getSeconds());
		this.getLogger().info("Time elapsed (ms)  -> "+d.toMillis());
		this.getLogger().info("----------------------------------");

	}

	private void sendRequest(Requests request, String urlCallBack) {
		try {				
			this.getLogger().info( Util.msg("Submitting -> ", request.getExperimentcode(), " - ", 
					String.valueOf(request.getExperimentvar()), " of ", String.valueOf(request.getVariation())) );
			this.getLogger().info(Util.msg("URL-> ",request.getUrl()));
			this.getLogger().info(Util.msg("Request -> ", request.getRequest()));
			HttpHeaders headers = Util.getDefaultHeaders();
			headers.add("ExperimentID", Util.msg(request.getExperimentcode(), ".", String.valueOf(request.getVariation())));
			headers.add("ExperimentVar", String.valueOf(request.getExperimentvar()));
			final String URL = (request.getUrl()==null) ? urlCallBack : request.getUrl(); 
			try {
				Util.sendRequest(URL, headers, HttpMethod.POST, request.getRequest(), Void.class);
			} catch (Exception e) {
				this.getLogger().info(e.getMessage());
				e.printStackTrace();
			}
			this.getLogger().info("Request submitted!");					
		} catch (Exception e) {
			this.getLogger().info(e.getMessage());
		}
	}

	private void generateFileResults(final String path, final String[] EdgeNodes, final int idxHost) {
		try {
			this.getLogger().info("Waiting 5s to start a new experiment execution...");
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.getLogger().info("-----------------------------------------------");
		this.getLogger().info("Generating file with the experiments results...");
		this.getLogger().info("-----------------------------------------------");

		if (idxHost == -1) {
			for (String en : EdgeNodes) {
				saveResults(en, Util.msg(path, en, "/"));
			}
		} else {
			saveResults(EdgeNodes[idxHost], Util.msg(path, EdgeNodes[idxHost], "/"));
		}
	}

	private void clearCacheMetrics(final String[] EdgeNodes, String experimentCode, int idxHost) {
		this.getLogger().info("-----------------------------------------------------------");
		this.getLogger().info(Util.msg("Preparing the environment to start the experiment [",experimentCode,"]..."));
		this.getLogger().info("-----------------------------------------------------------");
		
		if (idxHost == -1) {
			for (String en : EdgeNodes) {
				clearCacheSendRequest(en);
			}			
		} else {
			clearCacheSendRequest(EdgeNodes[idxHost]);
		}
	}

	private void clearCacheSendRequest(String en) {
		try {
			String managerApiUrl = 
					this.getUrl("http://", en, this.getPorts().getLwcoedge_manager_api(), "/lwcoedgemgr/metrics/experiment/clear");

			this.getLogger().info("Cleaning metrics..."+managerApiUrl);
			Util.sendRequest( managerApiUrl, Util.getDefaultHeaders(), HttpMethod.GET, null, Void.class);

			Thread.sleep(100);
			
			managerApiUrl = 
					this.getUrl("http://", en, this.getPorts().getLwcoedge_manager_api(), "/lwcoedgemgr/metrics/enable");

			this.getLogger().info("Enabling metrics..."+managerApiUrl);

			Util.sendRequest( managerApiUrl, Util.getDefaultHeaders(), HttpMethod.GET, null, Void.class);

		} catch (Exception e1) {
			this.getLogger().info(e1.getMessage());
		}							
	}

	private void activateCollaboration(final String[] EdgeNodes, final boolean value, final int idxHost) {
		this.getLogger().info("-----------------------------------------------------------");
		this.getLogger().info( Util.msg(" Changing collaboration process status to [", String.valueOf(value), "]") );
		this.getLogger().info("-----------------------------------------------------------");
		if (idxHost == -1) {
			for (String en : EdgeNodes) {
				try {
					String collaborationUrl = 
							this.getUrl("http://", en, this.getPorts().getLwcoedge_p2pcollaboration(), "/p2pcollaboration/enable/"+String.valueOf(value));
					sendRequestActivation(collaborationUrl);

				} catch (Exception e1) {
					this.getLogger().info("[activateCollaboration] "+e1.getMessage());
				}					
			}			
		} else {
			try {
				String collaborationUrl = 
						this.getUrl("http://", EdgeNodes[idxHost], this.getPorts().getLwcoedge_p2pcollaboration(), "/p2pcollaboration/enable/"+String.valueOf(value));
				sendRequestActivation(collaborationUrl);

			} catch (Exception e1) {
				this.getLogger().info("[activateCollaboration] "+e1.getMessage());
			}								
		}
	}

	private void activateDataSharing(final String[] EdgeNodes, final boolean value, final int idxHost) {
		this.getLogger().info("-----------------------------------------------------------");
		this.getLogger().info( Util.msg(" Changing data sharing process status to [", String.valueOf(value), "]") );
		this.getLogger().info("-----------------------------------------------------------");
		if (idxHost == -1) {
			for (String en : EdgeNodes) {
				try {
					String DataSharingUrl = 
						this.getUrl("http://", en, this.getPorts().getLwcoedge_p2pdatasharing(), "/p2pdatasharing/enable/"+String.valueOf(value));

					sendRequestActivation(DataSharingUrl);
					
				} catch (Exception e1) {
					this.getLogger().info("[activateDataSharing] "+e1.getMessage());
				}					
			}			
		} else {
			try {
				String DataSharingUrl = 
					this.getUrl("http://", EdgeNodes[idxHost], this.getPorts().getLwcoedge_p2pdatasharing(), "/p2pdatasharing/enable/"+String.valueOf(value));

				sendRequestActivation(DataSharingUrl);
				
			} catch (Exception e1) {
				this.getLogger().info("[activateDataSharing] "+e1.getMessage());
			}								
		}
	}

	private void sendRequestActivation(String url) throws Exception {
		this.getLogger().info(url);
		Util.sendRequest( url, Util.getDefaultHeaders(), HttpMethod.GET, null, Void.class);		
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void saveResults(String host, String path) {
		try {		
			ResponseEntity<ArrayList> httpResp = Util.sendRequest( Util.msg("http://", host, ":10500/lwcoedgemgr/metrics/results/keys"), 
					Util.getDefaultHeaders(), HttpMethod.GET, null, ArrayList.class);
			ArrayList<String> keys = httpResp.getBody();
			for (String key : keys) {
				this.getLogger().info( Util.msg(host,": Generating file to the key -> ",key));
				ResponseEntity<String> keyContent = Util.sendRequest( Util.msg("http://", host, ":10500/lwcoedgemgr/metrics/results/keys/",key), 
						Util.getDefaultHeaders(), HttpMethod.GET, null, String.class);
				try {
					//E1.200-M9
					String[] splitKey = key.split("\\.");
					String pathExperiment = Util.msg(path, splitKey[0], "/");
					File folder = new File(pathExperiment);
					if (!folder.exists()) {
						folder.mkdirs();
					}
			        FileWriter file = new FileWriter( Util.msg(pathExperiment, key, ".json"));
			        file.write(
			        		Util.msg("{", "\"value\"",":", keyContent.getBody(), "}")
			        );
			        file.flush();
			        file.close();
			    } catch (IOException e) {
			    	this.getLogger().info(e.getMessage());
			    }			
			}
		} catch (Exception e) {
			this.getLogger().info(e.getMessage());
		}
	}

}
