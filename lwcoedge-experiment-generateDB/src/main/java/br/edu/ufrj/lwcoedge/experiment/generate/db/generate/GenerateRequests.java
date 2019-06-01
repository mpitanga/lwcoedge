package br.edu.ufrj.lwcoedge.experiment.generate.db.generate;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ufrj.lwcoedge.core.model.Datatype;
import br.edu.ufrj.lwcoedge.core.model.Param;
import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.experiment.core.model.Collaboration;
import br.edu.ufrj.lwcoedge.experiment.core.model.ExperimentConfig;
import br.edu.ufrj.lwcoedge.experiment.generate.db.service.ServiceDB;

@Component
public class GenerateRequests implements ApplicationRunner {

	private Logger logger = LogManager.getLogger(getClass());
			//Logger.getLogger(this.getClass().getName());
	
	@Autowired
	ServiceDB service;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (args == null) {
			logger.info( "\n\n[ERROR] [GenerateRequests] LW-CoEdge experiment config parameters not configured!\n");
			System.exit(-1);
		}

		logger.info("[GenerateRequests] Loading the LW-CoEdge experiment settings...");
		
		String filePath = null;
		try {
			filePath = args.getOptionValues("experiment-config-filepath").get(0);
		} catch (Exception e) {
			logger.info( "\n\n[ERROR] The parameter [experiment-config-filepath] was not configured!\n" );
			System.exit(-1);
		}
		
		String[] fileNames = null;
		try {
			fileNames = args.getOptionValues("experiment-config-file").get(0).split(";");
		} catch (Exception e) {
			logger.info( "\n\n[ERROR] The parameter [experiment-config-file] was not configured!\n" );
			System.exit(-1);
		}

		for (String fileName : fileNames) {

			logger.info( Util.msg("[GenerateRequests] LW-CoEdge loading [",fileName, "] config file..."));
			ObjectMapper objectMapper = new ObjectMapper();
			ExperimentConfig config = objectMapper.readValue(new File(Util.msg(filePath,"/",fileName)), ExperimentConfig.class);
			logger.info("[GenerateRequests] LW-CoEdge experiment settings loaded.");

			String experimentName = config.getExperimentname();
			String callBackURL = config.getCallbackurl();
			String EntryPointPort = String.valueOf(config.getEntrypointport());
			String[] EdgeNodes = config.getEdgenodes();

			int times = config.getTimes();
			int idxHost = config.getIdxnode();
			int cycles = config.getCycles();
			int requestVariation = config.getRequestvariation();
			int minfreshness = config.getMinfreshness();
			int maxfreshness = config.getMaxfreshness();
			int maxresponsetime = config.getMaxresponsetime();
			String[] datatypeids = config.getDatatypeids();
			int idxdatatype = config.getIdxdatatype();
			Collaboration collabActivated = config.getCollaboration();
			
			if (collabActivated == null) {
				logger.info( "[GenerateRequests] The parameter 'Collaboration' was not configured!" );
				System.exit(1);
			}

			generateExperimentRequests(experimentName, EdgeNodes, EntryPointPort, callBackURL, 
					idxHost, times, cycles, requestVariation, minfreshness, maxfreshness, maxresponsetime,
					datatypeids, idxdatatype, collabActivated);
			
			logger.info("----------------------------------");
			logger.info("Waiting 5(s) to continue...");
			logger.info("----------------------------------");
			Thread.sleep(5000);
		}
	}

	private void generateExperimentRequests(String experimentName, String[] edgeNodes, String EntryPointPort, String callBackURL,
			int idxHost, int times, int cycles, int requestVariation, int minfreshness, int maxfreshness, int maxresponsetime,
			String[] datatypeids, int idxdatatype, Collaboration collabActivated) throws Exception {

		LocalDateTime startEx = LocalDateTime.now();

		int[] totalOfRequests = new int[cycles];
		
		for (int i = 0; i < cycles; i++) {
			totalOfRequests[i] = requestVariation*(i+1);
		}
		for(int x=0;x<times;x++) {
			int idx = (x+1);
			// Experiment - E1, E2,...
			String experimentCode = "E"+idx;
			
			//Clear requests
			service.clearRequest(experimentName, experimentCode);
			
			// alterar para o nome do experimento variar de acordo com X...E1,E2,E3...
			createRequest(edgeNodes, EntryPointPort, callBackURL, experimentName, experimentCode, cycles, requestVariation, 
					datatypeids, minfreshness, maxfreshness, maxresponsetime, idxHost, idxdatatype,
					totalOfRequests, collabActivated);

		}
		LocalDateTime finishEx = LocalDateTime.now();
		Duration d = Duration.between(startEx, finishEx);
		logger.info("----------------------------------");
		logger.info("Experiment Name    -> {}",experimentName);
		logger.info("Start experiment   -> {}",startEx);
		logger.info("Finish experiment  -> {}",finishEx);
		logger.info("Time elapsed (sec) -> {}",d.getSeconds());
		logger.info("Time elapsed (ms)  -> {}",d.toMillis());
		logger.info("----------------------------------");

	}

	private void createRequest(String[] edgeNodes, String entryPointPort, String callBackURL, 
			String experimentName, String experimentCode, int cycles, int requestVariation, String[] datatypeids, 
			int minfreshness, int maxfreshness, int maxresponsetime, int idxHost, int idxdatatype, 
			int[] totalOfRequests, Collaboration collabActivated) {

		LocalDateTime startEx = LocalDateTime.now();
		for (int i = 0; i < cycles; i++) {
			try {
				final int variation=requestVariation*(i+1);
				logger.info( Util.msg("Generation experiment: ", experimentCode, " variation: ", String.valueOf(variation)) );
				int activeRequests = (collabActivated.isActive()) 
						? (totalOfRequests[i] * collabActivated.getDatasharingactivated().getPercentageofrequests())/100
						: 0;

				doCreateRequest(edgeNodes, entryPointPort, experimentName, experimentCode, datatypeids, 
								minfreshness, maxfreshness, maxresponsetime, callBackURL, variation, 
								idxHost, idxdatatype, activeRequests, collabActivated);

			} catch (Exception e) {
				logger.info(e.getMessage());
			}
		}
		LocalDateTime finishEx = LocalDateTime.now();
		Duration d = Duration.between(startEx, finishEx);
		logger.info("----------------------------------");
		logger.info("Thread finished    -> "+experimentCode);
		logger.info("Start experiment   -> "+startEx);
		logger.info("Finish experiment  -> "+finishEx);
		logger.info("Time elapsed (sec) -> "+d.getSeconds());
		logger.info("Time elapsed (ms)  -> "+d.toMillis());
		logger.info("----------------------------------");
	}

	private void doCreateRequest(String[] edgeNodes, String EntryPointPort, String experimentname, String experimentcode,
			String[] datatypeIds, int minFreshness, int maxFreshness, int maxResponsetime, String callback, int variation, 
			int idxhost, int idxdatatype, int activeRequests, Collaboration collabActivated) {

		logger.info("-----------------------------------");
		logger.info( Util.msg("Generation Request-> Experiment: ",experimentname,", Variation: ", String.valueOf(variation)) );
		logger.info("-----------------------------------");

		try {
			int total = 0;
			boolean activatedatasharing = true;

			for(int i=0; i<variation; i++) {
				final int var = i+1;
				
				if (collabActivated.isActive()) {
					total += 1;
					if (activatedatasharing && total > activeRequests) {
						activatedatasharing = false;
					}
				}
				final boolean activateds = activatedatasharing;
				
//				Thread t = new Thread(()->{
					String hostIP = (idxhost == -1) 
							? edgeNodes[generateNumber(0, edgeNodes.length)]
							: edgeNodes[idxhost];
					String URLHost = Util.msg("http://", hostIP, ":", EntryPointPort, "/lwcoedge/request/send");
					int idxDT = (idxdatatype == -1)
							? generateNumber(0, datatypeIds.length)
							: idxdatatype;
					int freshness = (maxFreshness > minFreshness) ? generateNumber(minFreshness, maxFreshness) : maxFreshness;
					String experimentid = Util.msg(experimentcode, ".", String.valueOf(variation));
					int experimentvar = var;
					try {
						Request request = requestGenerator(datatypeIds[idxDT], freshness, maxResponsetime, callback);

						logger.info( Util.msg("Datatype id: ",datatypeIds[idxDT], " Freshness: ", String.valueOf(freshness)) );
						logger.info( Util.msg("Saving request -> ", experimentcode, " - ", String.valueOf(var), " of ", String.valueOf(variation)) );

						service.saveRequest(experimentname, 
								experimentcode, 
								variation,
								hostIP, 
								collabActivated.isActive(), 
								activateds, 
								idxhost, 
								experimentvar, 
								experimentid,
								URLHost,
								request);

					} catch (Exception e) {
						e.printStackTrace();
					}
					logger.info("Request registered!");					
//				});
/*				t.start();
				t.join();
				if (i>0) {
					Thread.sleep(10);
				}
*/			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Request requestGenerator(String datatypeID, Integer freshness, Integer responseTime, String callback) {
		return new Request(new Datatype(datatypeID), new Param(freshness, responseTime, 1), callback);		
	}

	private int generateNumber(int min, int max) {
		int r = (int) (Math.random() * max);
		return (r<min) ? min : r;
	}

}
