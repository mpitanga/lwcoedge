package br.edu.ufrj.lwcoedge.core.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmount;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmountAndTimes;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmountAndValue;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricCollected;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricComputationTime;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricIdentification;
import br.edu.ufrj.lwcoedge.core.model.ResponseBodyException;
import br.edu.ufrj.lwcoedge.core.util.Util;

@Service
public class SendMetricService extends AbstractService {

	@Async
	public void sendMetric(String managerApiUrl, String experimentId, String metricName, String datatypeId) {
		this.getLogger().debug( "Registering metric {} - {} to registry...", experimentId, metricName );
		MetricIdentification id = new MetricIdentification(experimentId, metricName, null, datatypeId);
		try {
			this.sendMetric(managerApiUrl, id.getKey(), id.toString(), 1);
		} catch (Exception e) {
			this.getLogger().error("[ERROR] Error submitting the metric [{}] to the registry. {}", id.toString(), e.getMessage());
		}
	}

	@Async
	public void sendMetricAnalytic(String managerApiUrl, String experimentId, String metricName, String fullRequestId, Long valueOf) {
		this.getLogger().debug( "Registering metric {} - {} to registry...", experimentId, metricName );
//		this.getLogger().info( Util.msg("Registering metric ", experimentId,"-",metricName, " to registry..."));
		MetricIdentification id = new MetricIdentification(fullRequestId);
		id.setMetric(metricName);
		String metricId = experimentId+"-"+metricName;
		try {
			this.sendMetric(managerApiUrl, metricId, id.toString(), valueOf.intValue());
		} catch (Exception e) {
			this.getLogger().error("[ERROR] Error submitting the metric [{}] to the registry.{}", metricId, e.getMessage());
		}
	}

	@Async
	public void sendMetricSummaryValue(String managerApiUrl, String experimentId, String metricName, String datatypeId, Long valueOf) {
		this.getLogger().debug( "Registering metric {} - {} to registry...", experimentId, metricName );
		//Bandwidth consumed
		MetricIdentification id = new MetricIdentification(experimentId, metricName, null, datatypeId);
		try {
			this.sendMetricSummaryValue(managerApiUrl, id.getKey(), id.toString(), valueOf);
		} catch (Exception e) {
			this.getLogger().error("[ERROR] Error submitting the metric [{}] to the registry. {}", id.toString(), e.getMessage());
		}
	}

	@Async
	public void sendMetricComputationalTime(String managerApiUrl, String experimentId, String metricName, String datatypeId, 
			LocalDateTime start, LocalDateTime finish) {
		this.getLogger().debug( "Registering metric {} - {} to registry...", experimentId, metricName );
		MetricIdentification id = new MetricIdentification(experimentId, metricName, null, datatypeId);
		try {
			this.sendMetricComputationalTime(managerApiUrl, id.getKey(), id.toString(), start, finish);					
		} catch (Exception e) {
			this.getLogger().error("[ERROR] Error submitting the metric [{}] to the registry. {}", id.toString(), e.getMessage());
		}
	}

	@Async
	public void sendMetricSummary(String managerApiUrl, String experimentId, String metricName, String datatypeId, LocalDateTime start, LocalDateTime finish) {
		this.getLogger().debug( "Registering metric {} - {} to registry...", experimentId, metricName );
		MetricIdentification id = new MetricIdentification(experimentId, metricName, null, datatypeId);
		try {
			// Summary
			this.sendMetricSummary(managerApiUrl, id.getKey(), id.getSummaryKey(), start, finish);
		} catch (Exception e) {
			this.getLogger().error("[ERROR] Error submitting the metric [{}] to the registry. {}", id.toString(), e.getMessage());
		}			
	}

	// auxiliar methods
	private void sendMetric(String url, String metricKey, String fullId, Integer value) throws Exception {
		MetricAmount metricAmount = new MetricAmount(fullId);
		metricAmount.setAmountOf(value);

		MetricCollected mc = new MetricCollected(metricKey);
		mc.setMetricAmount(metricAmount);
		try {
			Util.sendRequest(url, Util.getDefaultHeaders(), HttpMethod.POST, mc, Void.class);
		} catch (Exception e) {
			String exceptMsg;
			if (e instanceof HttpServerErrorException) {
				ResponseBodyException rbe = Util.json2obj(((HttpServerErrorException)e).getResponseBodyAsString(), ResponseBodyException.class);
				exceptMsg = rbe.getMessage();
			} else {
				exceptMsg = e.getMessage();
			}
			throw new Exception("[ERROR] Error sending the metric to the URL "+url+"."+exceptMsg);
		}
	}

	private void sendMetricComputationalTime(String url, String metricKey, String fullId, LocalDateTime start, LocalDateTime finish) throws Exception {
		MetricCollected mc = new MetricCollected(metricKey);
		mc.setMetricComputationalTime(new MetricComputationTime(fullId, start, finish));
		try {
			Util.sendRequest(url, Util.getDefaultHeaders(), HttpMethod.POST, mc, Void.class);
		} catch (Exception e) {
			throw new Exception("[ERROR] Error sending the metric to the URL "+url);
		}
	}

	private void sendMetricSummary(String url, String metricKey, String summaryId, LocalDateTime start, LocalDateTime finish) throws Exception {
		MetricCollected mc = new MetricCollected(metricKey);
		mc.setMetricAmountAndTimes(new MetricAmountAndTimes(summaryId, start, finish, 1));
		try {
			Util.sendRequest(url, Util.getDefaultHeaders(), HttpMethod.POST, mc, Void.class);
		} catch (Exception e) {
			throw new Exception("[ERROR] Error sending the metric to the URL "+url);
		}
	}

	private void sendMetricSummaryValue(String url, String metricKey, String summaryId, Long valueOf) throws Exception {
		MetricCollected mc = new MetricCollected(metricKey);
		mc.setMetricAmountAndValue(new MetricAmountAndValue(summaryId, valueOf, 1));
		try {
			Util.sendRequest(url, Util.getDefaultHeaders(), HttpMethod.POST, mc, Void.class);
		} catch (Exception e) {
			throw new Exception("[ERROR] Error sending the metric to the URL "+url);
		}
	}

}
