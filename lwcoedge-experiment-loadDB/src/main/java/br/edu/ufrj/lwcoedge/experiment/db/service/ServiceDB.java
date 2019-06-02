package br.edu.ufrj.lwcoedge.experiment.db.service;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmount;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmountAndValue;
import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.experiment.core.model.MetricAmountAndTimesValues;
import br.edu.ufrj.lwcoedge.experiment.core.model.MetricAmountAndValues;
import br.edu.ufrj.lwcoedge.experiment.core.model.MetricAmountValues;
import br.edu.ufrj.lwcoedge.experiment.core.model.MetricCT;
import br.edu.ufrj.lwcoedge.experiment.core.model.MetricCTAndAmount;
import br.edu.ufrj.lwcoedge.experiment.core.model.MetricComputationTimeValues;
import br.edu.ufrj.lwcoedge.experiment.db.dao.MetricsDAO;

@Service
public class ServiceDB {

	private Logger logger = LogManager.getLogger(getClass());
	
	private ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	MetricsDAO metricsDAO;

	@Transactional(propagation=Propagation.REQUIRED)
	public void loadDataToDB(String experimentName, String path, String[] edgeNodes, int times) {
	
		for(int x=0;x<times;x++) {
			// Experiment - E1, E2,...
			String experimentCode = "E"+(x+1);
			for (String en : edgeNodes) {

				String JsonExperimentPath = Util.msg(path,en,"/",experimentCode);
				File f = new File(JsonExperimentPath);
				if (!f.isDirectory()) {
					continue;
				}
				for (String fileName : f.list()) {
					//E2.10000-M1
					String[] filePartsName = fileName.split("-");
					try {
						String[] part1 = filePartsName[0].split("\\.");
						String[] part2 = filePartsName[1].split("\\.");
						final int variation= Integer.parseInt(part1[1]);
						logger.info( "Experiment name: {} Edge node: {} Loading data of experiment: {} variation: {}", experimentName, en, experimentCode, part1[1] );

						loadJsonMetricToDB(experimentName, JsonExperimentPath, en, experimentCode, variation, part2[0], fileName);
						
					} catch (Exception e) {
						logger.info("[loadDataToDB] {}",e.getMessage());
					}					
				}
			}
		}
	}

	private void loadJsonMetricToDB(String experimentName, String path, String en, String experimentCode, 
			int variation, String metric, String fileName) {
		logger.info( "Metric: {}", metric );
		String keyMetricName = Util.msg(path, "/", fileName);
		try {
			File f = new File(keyMetricName);
			if (f.exists()) {
				if (metric.equals("REQ_MET")||metric.equals("REQ_NOT_MET")||metric.equals("REQ_SENTTO_NB")||metric.equals("R_TEMP_DB")
						||metric.equals("R_ENDEV")||metric.equals("R_MEM_CACHE")||metric.equals("REQ_RTTH_INV")
						||metric.indexOf("_ANL")>0) {
					loadJsonMetricAmountToDB(experimentName, f, en, keyMetricName, variation);
				} else {
					//3,5,6 
					if (metric.equals("TIME_REQ")||metric.equals("TIME_SPENT_FW")||metric.equals("COMM_TIME")
							||metric.equals("TIME_SELECT_NB")) {
						loadJsonMetricAmountandTimeToDB(experimentName, f, en, keyMetricName, variation);
					} else {
						if (metric.equals("DEPLOY")) {
							loadJsonMetricAmountandDateTimeToDB(experimentName, f, en, keyMetricName, variation);
						} else {
							//M7 e M12
							loadJsonMetricAmountAndValuesToDB(experimentName, f, en, keyMetricName, variation);							
						}
					}
				}
			} else {
				logger.info( "Metrics file not found! [{}]",keyMetricName );
			}
		} catch (Exception e) {
			logger.info("[loadJsonMetricToDB] {}",e.getMessage());
		}
	}

	private void loadJsonMetricAmountAndValuesToDB(String experimentName, File f, String edgeNode, String keyMetricName, int variation) throws Exception {
		MetricAmountAndValues mav = objectMapper.readValue(f, MetricAmountAndValues.class);
		for (MetricAmountAndValue metricAmountAndValue : mav.getValue()) {
			logger.info("ID : {}", metricAmountAndValue.getId());
			metricsDAO.saveMetricAmountAndValue(experimentName, edgeNode, metricAmountAndValue, variation);
		}
	}

	private void loadJsonMetricAmountandTimeToDB(String experimentName, File f, String edgeNode, String keyMetricName, int variation) throws Exception {
		MetricAmountAndTimesValues mav = objectMapper.readValue(f, MetricAmountAndTimesValues.class);
		for (MetricCTAndAmount metricCTAndAmount : mav.getValue()) {
			logger.info("ID : {}" , metricCTAndAmount.getId());
			metricsDAO.saveMetricComputationTime(experimentName, edgeNode, metricCTAndAmount, variation);
		}
	}

	private void loadJsonMetricAmountToDB(String experimentName, File f, String edgeNode, String keyMetricName, int variation) throws Exception {
		MetricAmountValues mav = objectMapper.readValue(f, MetricAmountValues.class);
		for (MetricAmount metricAmount : mav.getValue()) {
			logger.info("ID : {}" , metricAmount.getId());
			metricsDAO.saveMetricAmount(experimentName, edgeNode, metricAmount, variation);
		}
	}

	private void loadJsonMetricAmountandDateTimeToDB(String experimentName, File f, String edgeNode, String keyMetricName, int variation) throws Exception {
		MetricComputationTimeValues mav = objectMapper.readValue(f, MetricComputationTimeValues.class);
		for (MetricCT metricCT : mav.getValue()) {
			logger.info("ID : {}" , metricCT.getId());
			metricsDAO.saveMetricComputationTimeValues(experimentName, edgeNode, metricCT, variation);
		}
	}

}
