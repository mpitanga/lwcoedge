package br.edu.ufrj.lwcoedge.experiment.request;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.icmp4j.IcmpPingRequest;
import org.icmp4j.IcmpPingResponse;
import org.icmp4j.IcmpPingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmount;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmountAndValue;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricIdentification;
import br.edu.ufrj.lwcoedge.core.model.Datatype;
import br.edu.ufrj.lwcoedge.core.model.Param;
import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.core.service.AbstractService;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.experiment.core.model.Collaboration;
import br.edu.ufrj.lwcoedge.experiment.core.model.ExperimentConfig;
import br.edu.ufrj.lwcoedge.experiment.core.model.MetricAmountAndTimesValues;
import br.edu.ufrj.lwcoedge.experiment.core.model.MetricAmountAndValues;
import br.edu.ufrj.lwcoedge.experiment.core.model.MetricAmountValues;
import br.edu.ufrj.lwcoedge.experiment.core.model.MetricCTAndAmount;
import br.edu.ufrj.lwcoedge.experiment.graph.CSVFile;

@Component
public class Experiment0 extends AbstractService  {

	@Autowired
	private CSVFile csv;

	public void run(ApplicationArguments args) throws Exception {
		
		this.getLogger().info("Loading the LW-CoEdge experiment settings...");
		String fileName = args.getOptionValues("experiment-config").get(0);
		ObjectMapper objectMapper = new ObjectMapper();
		ExperimentConfig config = objectMapper.readValue(new File(fileName), ExperimentConfig.class);
		this.getLogger().info("LW-CoEdge experiment settings loaded.");
		
		this.loadComponentsPort(args);

		String callBackURL = config.getCallbackurl();
		String experimentName = config.getExperimentname();
		String basePath = config.getBasepath();
		String path = basePath+experimentName+"/";
		String[] EdgeNodes = config.getEdgenodes();
		String EntryPointPort = String.valueOf(config.getEntrypointport());

		int times = config.getTimes();
		boolean executeExperiment = config.isExecuteexperiment();
		boolean clearMetrics = config.isClearmetrics();
		boolean generateResults = config.isGenerateresults();
		int idxHost = config.getIdxnode();

		int cycles = config.getCycles();
		int requestVariation = config.getRequestvariation();
		int maxfreshness = config.getMaxfreshness();
		int maxresponsetime = config.getMaxresponsetime();
		boolean randomFreshness = config.isRandomfreshness();
		String[] datatypeids = config.getDatatypeids();
		int idxdatatype = config.getIdxdatatype();
		Collaboration collabActivated = config.getCollaboration();
		int waitToGenerate = (config.getWaittogenerate() == null || config.getWaittogenerate() == 0) ? 5 * 60 : config.getWaittogenerate();

		if (collabActivated == null) {
			this.getLogger().info( "The parameter [Collaboration] was not configured!" );
			System.exit(1);
		}
		
		runExperiment(
				experimentName,
				path, 
				EdgeNodes, 
				EntryPointPort, 
				callBackURL, 
				executeExperiment, 
				clearMetrics, 
				generateResults, 
				idxHost,
				times,
				cycles,
				requestVariation,
				maxfreshness,
				maxresponsetime,
				randomFreshness,
				datatypeids,
				idxdatatype,
				collabActivated,
				waitToGenerate
		);
	}

	private void runExperiment(String experimentName, String path, String[] EdgeNodes, 
			String EntryPointPort, String callBackURL, 
			boolean executeExperiment, 
			boolean clearMetrics, 
			boolean generateResults, 
			int idxHost,
			int times,
			int cycles,
			int requestVariation,
			int maxfreshness,
			int maxresponsetime,
			boolean randomFreshness,
			String[] datatypeIds, int idxdatatype,
			Collaboration collabActivated, int waitToGenerate) throws JsonParseException, JsonMappingException, IOException {
	
		if (executeExperiment) {
			LocalDateTime startEx = LocalDateTime.now();

			int[] totalOfRequests = new int[cycles];
			
			for (int i = 0; i < cycles; i++) {
				totalOfRequests[i] = requestVariation*(i+1);
			}

			if (clearMetrics) {
				clearCacheMetrics(EdgeNodes, "E0", -1);
			}

			for(int x=0;x<times;x++) {
				activateCollaboration(EdgeNodes, collabActivated.isActive(), idxHost);
				//activateDataSharing(EdgeNodes, collabActivated.isActive());
				
				int idx = (x+1);
				
				// Experiment - E1, E2,...
				String experimentCode = "E"+idx;
/*
				if (clearMetrics) {
					clearCacheMetrics(EdgeNodes, experimentCode, -1);
				}
*/
				// alterar para o nome do experimento variar de acordo com X...E1,E2,E3...
				startExperiment1(EdgeNodes, EntryPointPort, callBackURL, experimentCode, cycles, requestVariation, datatypeIds, maxfreshness,
						maxresponsetime, randomFreshness, idxHost, idxdatatype, totalOfRequests, collabActivated);
/*
				if (generateResults) {
					generateFileResults(path, EdgeNodes, -1idxHost, waitToGenerate);
				}
*/
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
		if (generateResults) {
			generateFileResults(path, EdgeNodes, idxHost, waitToGenerate);
		}
	}

	@Async("threadPoolTaskExecutor_Experiment")
	private void startExperiment1(String[] EdgeNodes, String lwcoedgeHostPort, String callBackURL,
			String experiment, int cycles, int requestVariation, String[] datatypeIds, 
			int maxFreshness, int maxResponsetime, boolean randomFreshness, 
			int idxHost, int idxdatatype, int[] totalOfRequests, 
			Collaboration collabActivated) {
		
		LocalDateTime startEx = LocalDateTime.now();

		for (int i = 0; i < cycles; i++) {
			try {
				activateDataSharing(EdgeNodes, collabActivated.isActive(), idxHost);

				final int v=requestVariation*(i+1);
				this.getLogger().info( Util.msg("Generation experiment: ", experiment, " variation: ", String.valueOf(v)) );
				int activeRequests = (collabActivated.isActive()) 
						? (totalOfRequests[i] * collabActivated.getDatasharingactivated().getPercentageofrequests())/100
						: 0;
				sendRequest1(
						EdgeNodes,
						lwcoedgeHostPort,
						experiment, 
						datatypeIds, 
						maxFreshness,
						maxResponsetime,
						callBackURL, 
						v,
						randomFreshness, 
						idxHost,
						idxdatatype,
						activeRequests,
						collabActivated
				);
			} catch (Exception e) {
				this.getLogger().info(e.getMessage());
			}
			try {
				this.getLogger().info("Waiting 5s to start a new cycle...");
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		LocalDateTime finishEx = LocalDateTime.now();
		Duration d = Duration.between(startEx, finishEx);
		this.getLogger().info("----------------------------------");
		this.getLogger().info("Thread finished    -> "+experiment);
		this.getLogger().info("Start experiment   -> "+startEx);
		this.getLogger().info("Finish experiment  -> "+finishEx);
		this.getLogger().info("Time elapsed (sec) -> "+d.getSeconds());
		this.getLogger().info("Time elapsed (ms)  -> "+d.toMillis());
		this.getLogger().info("----------------------------------");
	}
	
	final IcmpPingRequest pingRequest = IcmpPingUtil.createIcmpPingRequest();
	private long ping(String host, int times, int packetSize) {
		this.pingRequest.setHost (host);
		this.pingRequest.setPacketSize(packetSize);

		int time = 0;
		// repeat a few times
		for (int count = 0; count < times; count ++) {
			// delegate
			final IcmpPingResponse response = IcmpPingUtil.executePingRequest(this.pingRequest);
			time += response.getRtt();
		}
		Double latency = (double)time/times;
		return latency.longValue() ;
	}
	
	private void sendRequest1(String[] EdgeNodes, String lwcoedgeHostPort, String experiment, String[] datatypeIds, 
			int maxFreshness, int maxResponsetime, String callback, int variation, boolean randomFreshness, 
			int idxHost, int idxdatatype, int activeRequests, Collaboration collabActivated) {
		this.getLogger().info("-----------------------------------");
		this.getLogger().info( Util.msg("Starting Experiment: ",experiment,", Variation: ", String.valueOf(variation)) );
		this.getLogger().info("-----------------------------------");

		try {
			int total = 0;
			boolean executeActivate = true;

			long latency = ping (EdgeNodes[0], 4, 32);
			if (latency > 50)
				latency = 0;
			
			for(int i=0; i<variation; i++) {
				final int var = i+1;
				
				if (collabActivated.isActive()) {
					total += 1;
					if (executeActivate && total > activeRequests) {
						executeActivate = false;
						activateDataSharing(EdgeNodes, false, idxHost);
					}					
				}
				
				String hostIP = (idxHost == -1) 
						? EdgeNodes[generateNumber(0, EdgeNodes.length)]
								: EdgeNodes[idxHost];
				String URL = Util.msg("http://", hostIP, ":", lwcoedgeHostPort, "/lwcoedge/request/send");
				int idxDT = (idxdatatype == -1) ? generateNumber(0, datatypeIds.length) : idxdatatype;
				int freshness = (randomFreshness) ? generateNumber(1000, maxFreshness) : maxFreshness;
				Request request = requestGenerator(datatypeIds[idxDT], freshness, maxResponsetime, callback);
				this.getLogger().info( Util.msg("Datatype id: ",datatypeIds[idxDT], " Freshness: ", String.valueOf(freshness)) );
				this.getLogger().info( Util.msg("Submitting request -> ", experiment, " - ", String.valueOf(var), " of ", String.valueOf(variation)) );
				HttpHeaders headers = Util.getDefaultHeaders();
				headers.add("ExperimentID", Util.msg(experiment, ".", String.valueOf(variation)));
				headers.add("ExperimentVar", String.valueOf(var));
				try {
					Util.sendRequest(URL, headers, HttpMethod.POST, request, Void.class);
				} catch (Exception e) {
					e.printStackTrace();
				}
				this.getLogger().info("Request submitted!");					
				Thread.sleep(latency);
			}
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

	private void generateFileResults(final String path, final String[] EdgeNodes, final int idxHost, int waitToGenerate) {
		this.getLogger().info("Waiting "+waitToGenerate+"(s) to start a new experiment execution...");
		try {
			Thread.sleep(waitToGenerate*1000);
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
					this.getLogger().info( Util.msg("Body: ", keyContent.getBody()));
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

	private ArrayList<Float> getZeros(int cycles) {
		ArrayList<Float> zeros = new ArrayList<Float>();
		for (int i=0; i<cycles;i++) {
			zeros.add(0.0f);
		}
		return zeros;
	}
	
	public void generateGraphMetricAmount(String path, String metric, String[] labels,
				String experiment, int times, int cycles, int variation, String[] datatypeIds) {
		
		Map<String, ArrayList<Float>> subtitleValues = new HashMap<String, ArrayList<Float>>();
		Map<String, ArrayList<Float>> subtitleAnalyticValues = new HashMap<String, ArrayList<Float>>();
		Map<String, ArrayList<Float>> confidenceInterval  = new HashMap<String, ArrayList<Float>>();

		Map<String, Float> avarege  = new HashMap<String, Float>();
		Map<String, Float> dp  = new HashMap<String, Float>();

		ArrayList<String> subtitles = new ArrayList<String>();

		List<String> distinctDatatypeIds = Arrays.asList(datatypeIds).stream().distinct().collect(Collectors.toList());
		
		for (String datatypeId : distinctDatatypeIds) {
			subtitles.add(datatypeId);
			subtitleValues.put(datatypeId, getZeros(cycles));
			subtitleAnalyticValues.put(datatypeId, getZeros(times));
			confidenceInterval.put(datatypeId, getZeros(cycles));
			dp.put(datatypeId, 0.0f);
			avarege.put(datatypeId, 0.0f);
		}

		ArrayList<String> variationValues = new ArrayList<String>();
		ObjectMapper objectMapper = new ObjectMapper();
		for (int i = 0; i < cycles; i++) {
			final int v=variation*(i+1);			
			variationValues.add( String.valueOf( (float)v ) );

			for (int t=0; t<times;t++) {
				String exp = experiment+(t+1);
				final String keyMetricName = Util.msg(path, exp, "/", exp, ".", String.valueOf(v), "-", metric, ".json");
				try {
					File f = new File(keyMetricName);
					MetricAmountValues mav = objectMapper.readValue(f, MetricAmountValues.class);
					for (MetricAmount metricAmount : mav.getValue()) {
						MetricIdentification mi = new MetricIdentification(metricAmount.getId());
						ArrayList<Float> amountOf = subtitleValues.get(mi.getDatatypeID());
						float sum = amountOf.get(i) + metricAmount.getAmountOf().floatValue();
						amountOf.set(i, sum);
						
						ArrayList<Float> analyticAmountOf = subtitleAnalyticValues.get(mi.getDatatypeID());
						analyticAmountOf.set(t, metricAmount.getAmountOf().floatValue());

					}
				} catch (IOException e) {
					this.getLogger().info(e.getMessage());
				}
			}
			for (String datatypeId : distinctDatatypeIds) {
				ArrayList<Float> amountOf = subtitleValues.get(datatypeId);
				float avg = amountOf.get(i) / times;
				amountOf.set(i, avg);				
			}		
		}
		
		//calcConfidenceInterval(cycles, datatypeIds, subtitleValues, confidenceInterval, avarege, dp);

		csv.graph(Util.msg(path, experiment, "-", metric, ".csv"), labels[0], labels[1], labels[2], variationValues, subtitles, subtitleValues, confidenceInterval);
		
	}

	public void generateMetricComputationTime(String path, String metric, String[] labels,
			String experiment, int times, int cycles, int variation, String[] datatypeIds, boolean summary) {
	
		Map<String, ArrayList<Float>> subtitleValues = new HashMap<String, ArrayList<Float>>();
		Map<String, ArrayList<Float>> subtitleAnalyticValues = new HashMap<String, ArrayList<Float>>();
		Map<String, ArrayList<Float>> confidenceInterval  = new HashMap<String, ArrayList<Float>>();
		//Map<String, ArrayList<Float>> amountOf  = new HashMap<String, ArrayList<Float>>();
		Map<String, ArrayList<Float>> amountAnalyticOf  = new HashMap<String, ArrayList<Float>>();
	
		ArrayList<String> subtitles = new ArrayList<String>();
		List<String> distinctDatatypeIds = Arrays.asList(datatypeIds).stream().distinct().collect(Collectors.toList());

		for (String datatypeId : distinctDatatypeIds) {
			subtitles.add(datatypeId);
			subtitleValues.put(datatypeId, getZeros(cycles));
			subtitleAnalyticValues.put(datatypeId, getZeros(times));
			confidenceInterval.put(datatypeId, getZeros(cycles));
			//amountOf.put(datatypeId, getZeros(cycles));
			amountAnalyticOf.put(datatypeId, getZeros(times));
		}
		ArrayList<String> variationValues = new ArrayList<String>();
		ObjectMapper objectMapper = new ObjectMapper();
		
		for(int i = 1; i<=cycles; i++) {
			final int v=variation*i;			
			variationValues.add( String.valueOf( (float)v ) );

			for (int t=0; t<times;t++) {
				String exp = experiment+(t+1);
				final String keyMetricName = Util.msg(path, exp, "/", exp, ".", String.valueOf(v), "-", metric, ".json");
				try {
					File f = new File(keyMetricName);
					if (summary) {
						MetricAmountAndTimesValues mavgs = objectMapper.readValue(f, MetricAmountAndTimesValues.class);
						for (MetricCTAndAmount mavg : mavgs.getValue()) {
							MetricIdentification mi = new MetricIdentification(mavg.getId());
							ArrayList<Float> sumOf = subtitleValues.get(mi.getDatatypeID());
							//ArrayList<Float> amount = amountOf.get(mi.getDatatypeID());
							int index = (i-1);
							
							float div = (mavg.getAmountOf().floatValue() == 0) ? 1 : mavg.getAmountOf().floatValue();
							float avg = mavg.getComputationinMillis().floatValue() / div;
							sumOf.set(index, sumOf.get(index) + avg );
							//amount.set(index, amount.get(index) +  );

							ArrayList<Float> sbAnalyticOf = subtitleAnalyticValues.get(mi.getDatatypeID());
							sbAnalyticOf.set(t, mavg.getComputationinMillis().floatValue() );

							ArrayList<Float> aaOf = amountAnalyticOf.get(mi.getDatatypeID());
							aaOf.set(t, mavg.getAmountOf().floatValue() );

						}
					}
				} catch (Exception e) {
					this.getLogger().info(e.getMessage());
				}
			}
		}
		for (String datatypeId : distinctDatatypeIds) {
			ArrayList<Float> sumOf = subtitleValues.get(datatypeId);
			//ArrayList<Float> amount = amountOf.get(datatypeId);
			for (int j=0; j<cycles; j++) {
				//Float avg = sumOf.get(j) / ( (amount.get(j) == 0) ? 1 : amount.get(j) );
				Float avg = sumOf.get(j) / times;
				sumOf.set(j, avg);
			}
		}
		csv.graph(Util.msg(path, experiment, "-", metric, ".csv"), labels[0], labels[1], labels[2], variationValues, subtitles, subtitleValues, confidenceInterval);		
	}

	public void generateMetricAmountAndValues(String path, String metric, String[] labels,
			String experiment, int times, int cycles, int variation, String[] datatypeIds, boolean summary) {
	
		Map<String, ArrayList<Float>> subtitleValues = new HashMap<String, ArrayList<Float>>();
		Map<String, ArrayList<Float>> subtitleAnalyticValues = new HashMap<String, ArrayList<Float>>();
		Map<String, ArrayList<Float>> confidenceInterval  = new HashMap<String, ArrayList<Float>>();
		Map<String, ArrayList<Float>> amountAnalyticOf  = new HashMap<String, ArrayList<Float>>();
	
		ArrayList<String> subtitles = new ArrayList<String>();
		List<String> distinctDatatypeIds = Arrays.asList(datatypeIds).stream().distinct().collect(Collectors.toList());

		for (String datatypeId : distinctDatatypeIds) {
			subtitles.add(datatypeId);
			subtitleValues.put(datatypeId, getZeros(cycles));
			subtitleAnalyticValues.put(datatypeId, getZeros(times));
			confidenceInterval.put(datatypeId, getZeros(cycles));
			amountAnalyticOf.put(datatypeId, getZeros(times));
		}
		ArrayList<String> variationValues = new ArrayList<String>();
		ObjectMapper objectMapper = new ObjectMapper();
		
		for(int i = 1; i<=cycles; i++) {
			final int v=variation*i;			
			variationValues.add( String.valueOf( (float)v ) );

			for (int t=0; t<times;t++) {
				String exp = experiment+(t+1);
				final String keyMetricName = Util.msg(path, exp, "/", exp, ".", String.valueOf(v), "-", metric, ".json");
				try {
					File f = new File(keyMetricName);
					if (summary) {
						MetricAmountAndValues mavgs = objectMapper.readValue(f, MetricAmountAndValues.class);
						for (MetricAmountAndValue mavg : mavgs.getValue()) {
							MetricIdentification mi = new MetricIdentification(mavg.getId());
							ArrayList<Float> sumOf = subtitleValues.get(mi.getDatatypeID());
							//ArrayList<Float> amount = amountOf.get(mi.getDatatypeID());
							int index = (i-1);
							
							float div = (mavg.getAmountOf().floatValue() == 0) ? 1 : mavg.getAmountOf().floatValue();
							float avg = mavg.getValueOf().floatValue() / div;
							sumOf.set(index, sumOf.get(index) + avg );
							//amount.set(index, amount.get(index) +  );

							ArrayList<Float> sbAnalyticOf = subtitleAnalyticValues.get(mi.getDatatypeID());
							sbAnalyticOf.set(t, mavg.getValueOf().floatValue() );

							ArrayList<Float> aaOf = amountAnalyticOf.get(mi.getDatatypeID());
							aaOf.set(t, mavg.getAmountOf().floatValue() );

						}
					}
				} catch (Exception e) {
					this.getLogger().info(e.getMessage());
				}
			}
		}
		for (String datatypeId : distinctDatatypeIds) {
			ArrayList<Float> sumOf = subtitleValues.get(datatypeId);
			//ArrayList<Float> amount = amountOf.get(datatypeId);
			for (int j=0; j<cycles; j++) {
				//Float avg = sumOf.get(j) / ( (amount.get(j) == 0) ? 1 : amount.get(j) );
				Float avg = sumOf.get(j) / times;
				sumOf.set(j, avg);
			}
		}
		csv.graph(Util.msg(path, experiment, "-", metric, ".csv"), labels[0], labels[1], labels[2], variationValues, subtitles, subtitleValues, confidenceInterval);		
	}

	public void calcConfidenceInterval(int cycles, String[] datatypeIds, 
			Map<String, ArrayList<Float>> subtitleValues,
			Map<String, ArrayList<Float>> confidenceInterval,
			Map<String, Float> avarege, Map<String, Float> dp) {
		
		/* 
		Em python o calculo de intervalos de confiança de 95% para uma dada curva pode ser feito assim:

		metrics_ci_reg = 1.96 * metrics_std_reg / math.sqrt(n_reps) # Converts standard deviations to 95% confidence interval

		metrics_ci_reg  é uma lista que guarda os valores de intervalos de confiança respectivos a cada ponto de uma curva
		n_reps  é um inteiro, é o numero de repetições do experimento usado para obtenção dos valores médios e desvio padrão de cada ponto
		metrics_std_reg é uma lista  com os desvios padrão relativos a cada ponto no gráfico
		1.96 é a constante da distribuição normal escolhida pra 95% de confiança
		*/

		List<String> distinctDatatypeIds = Arrays.asList(datatypeIds).stream().distinct().collect(Collectors.toList());

		// avarege calc
		for (String datatypeId : distinctDatatypeIds) {
			float sum = 0;
			ArrayList<Float> amountOf = subtitleValues.get(datatypeId);
			for (int i = 0; i < cycles; i++) {
				sum += amountOf.get(i);
			}
			avarege.put(datatypeId, (sum/cycles));
		}
		// dp calc
		for (String datatypeId : distinctDatatypeIds) {
			float sum = 0;
			float avg = avarege.get(datatypeId);
			ArrayList<Float> amountOf = subtitleValues.get(datatypeId);
			for (int i = 0; i < cycles; i++) {
				float r = (amountOf.get(i) - avg);
				r = (r <0) ? r*-1 : r;
				sum += Math.pow(r, 2);
			}
			dp.put(datatypeId, (float)Math.sqrt(sum/cycles));
		}
		// confiance interval
		for (String datatypeId : distinctDatatypeIds) {
			ArrayList<Float> confInt = confidenceInterval.get(datatypeId);
			for (int i = 0; i < cycles; i++) {
				double interval = 1.96 * dp.get(datatypeId) / Math.sqrt(cycles);
				confInt.set(i, (float)interval);
			}
			confidenceInterval.put(datatypeId, confInt);
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

	private void sendRequestActivation(String url) throws Exception {
		this.getLogger().info(url);
		Util.sendRequest( url, Util.getDefaultHeaders(), HttpMethod.GET, null, Void.class);		
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


/*
	private void generateGraphFromResult(final String experiment, final String path, final int times, final int cycles,
			final int variation, final String[] datatypeIds)
			throws JsonParseException, JsonMappingException, IOException {
		this.getLogger().info("Generating the graphic to the metric M1...");
		String[] labelsM1 = {"Number of virtual nodes allocated - M1", "Number of requests", "Virtual Nodes"};
		generateGraphMetricAmount(path, "M1", labelsM1, experiment, times, cycles, variation, datatypeIds);

		this.getLogger().info("Generating the graphic to the metric M2...");
		String[] labelsM2 = {"Number of requests not met - M2", "Number of requests", "Requests"};
		generateGraphMetricAmount(path, "M2", labelsM2, experiment, times, cycles, variation, datatypeIds);

		this.getLogger().info("Generating the graphic to the metric M3...");
		String[] labelsM3 = {"Computation time to handle a request at the same edge node - M3", "Number of requests", "Avarage time (ms)"};
		generateMetricComputationTime(path, "M3", labelsM3, experiment, times, cycles, variation, datatypeIds, true);

		this.getLogger().info("Generating the graphic to the metric M4...");
		String[] labelsM4 = {"Number of requests that were forwarded to a neighboring edge node - M4", "Number of requests", "Requests"};
		generateGraphMetricAmount(path, "M4", labelsM4, experiment, times, cycles, variation, datatypeIds);

		this.getLogger().info("Generating the graphic to the metric M5...");
		String[] labelsM5 = {"Computation time to handle a request that was forwarded to a neighboring edge node - M5", "Number of requests", "Avarage time (ms)"};
		generateMetricComputationTime(path, "M5", labelsM5, experiment, times, cycles, variation, datatypeIds, true);

		this.getLogger().info("Generating the graphic to the metric M6...");
		String[] labelsM6 = {"Communication time (edge to edge) - M6", "Number of requests", "Avarage time (ms)"};
		generateMetricComputationTime(path, "M6", labelsM6, experiment, times, cycles, variation, datatypeIds, true);

		this.getLogger().info("Generating the graphic to the metric M7...");
		String[] labelsM7 = {"Average bandwidth consumed during the collaboration - M7", "Number of requests", "Avarage bandwidth (bytes)"};
		generateMetricAmountAndValues(path, "M7", labelsM7, experiment, times, cycles, variation, datatypeIds, true);

		this.getLogger().info("Generating the graphic to the metric M8...");
		String[] labelsM8 = {"Number of requests served (data read from database) - M8", "Number of requests", "Requests served"};
		generateGraphMetricAmount(path, "M8", labelsM8, experiment, times, cycles, variation, datatypeIds);

		this.getLogger().info("Generating the graphic to the metric M9...");
		String[] labelsM9 = {"Number of requests served (data read from end device) - M9", "Number of requests", "Requests served"};
		generateGraphMetricAmount(path, "M9", labelsM9, experiment, times, cycles, variation, datatypeIds);

		this.getLogger().info("Generating the graphic to the metric M10...");
		String[] labelsM10 = {"Number of requests served (data cache into virtual node memory) - M10", "Number of requests", "Requests served"};
		generateGraphMetricAmount(path, "M10", labelsM10, experiment, times, cycles, variation, datatypeIds);

	}
*/


//	public static void main(String[] args) {
/*		Experiment0 exp = new Experiment0();
		Request r = exp.requestGenerator("UFRJ.UbicompLab.temperature", 5000, "http://192.168.146.1:80/myapp/callback/result");
		System.out.println(r.toString());
		System.out.println(r.toString().length());
*/
/*		Experiment0 exp = new Experiment0();
		ArrayList<String> datatypeIds = new ArrayList<String>();
		datatypeIds.add("UFRJ.UbicompLab.temperature");
		datatypeIds.add("UFRJ.UbicompLab.humidity");
		datatypeIds.add("UFRJ.UbicompLab.luminosity");
		datatypeIds.add("UFRJ.UbicompLab.smoke");

		for (int i=0; i<100; i++) {
			System.out.println(exp.generateNumber(0, datatypeIds.size()));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
*/		
//	}

}
