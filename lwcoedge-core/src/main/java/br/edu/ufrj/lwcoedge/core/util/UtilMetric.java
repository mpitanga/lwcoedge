package br.edu.ufrj.lwcoedge.core.util;

import java.time.LocalDateTime;

import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpServerErrorException;

import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmount;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmountAndTimes;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmountAndValue;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricCollected;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricComputationTime;
import br.edu.ufrj.lwcoedge.core.model.ResponseBodyException;

public class UtilMetric {

	public static void sendMetric(String url, String metricKey, String fullId, Integer value) throws Exception {
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
			throw new Exception(Util.msg("[ERROR] ","Error sending the metric to the URL ",url, ".", exceptMsg));
		}
	}

	public static void sendMetricComputationalTime(String url, String metricKey, String fullId, LocalDateTime start, LocalDateTime finish) throws Exception {
		MetricCollected mc = new MetricCollected(metricKey);
		mc.setMetricComputationalTime(new MetricComputationTime(fullId, start, finish));
		try {
			Util.sendRequest(url, Util.getDefaultHeaders(), HttpMethod.POST, mc, Void.class);
		} catch (Exception e) {
			throw new Exception(Util.msg("[ERROR] ","Error sending the metric to the URL ",url));
		}
	}

	public static void sendMetricSummary(String url, String metricKey, String summaryId, LocalDateTime start, LocalDateTime finish) throws Exception {
		MetricCollected mc = new MetricCollected(metricKey);
		mc.setMetricAmountAndTimes(new MetricAmountAndTimes(summaryId, start, finish, 1));
		try {
			Util.sendRequest(url, Util.getDefaultHeaders(), HttpMethod.POST, mc, Void.class);
		} catch (Exception e) {
			throw new Exception(Util.msg("[ERROR] ","Error sending the metric to the URL ",url));
		}
	}

	public static void sendMetricSummaryValue(String url, String metricKey, String summaryId, Long valueOf) throws Exception {
		MetricCollected mc = new MetricCollected(metricKey);
		mc.setMetricAmountAndValue(new MetricAmountAndValue(summaryId, valueOf, 1));
		try {
			Util.sendRequest(url, Util.getDefaultHeaders(), HttpMethod.POST, mc, Void.class);
		} catch (Exception e) {
			throw new Exception(Util.msg("[ERROR] ","Error sending the metric to the URL ",url));
		}
	}

}
