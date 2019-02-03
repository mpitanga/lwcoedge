package br.edu.ufrj.lwcoedge.core.metrics.experiment;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

import br.edu.ufrj.lwcoedge.core.util.Util;

public class MetricComputationTime extends AbstractMetric implements Serializable {
	private static final long serialVersionUID = 8178787511604209006L;
	
	private LocalDateTime start;
	private LocalDateTime finish;

	public MetricComputationTime() {}

	public MetricComputationTime(String id) {
		super(id);
	}
	
	public MetricComputationTime(String id, LocalDateTime start, LocalDateTime finish) {
		super(id);
		this.start = start;
		this.finish = finish;
	}

	/**
	 * @return the start
	 */
	public LocalDateTime getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	/**
	 * @return the finish
	 */
	public LocalDateTime getFinish() {
		return finish;
	}

	/**
	 * @param finish the finish to set
	 */
	public void setFinish(LocalDateTime finish) {
		this.finish = finish;
	}

	public Long getComputationinMillis() {
		Duration d = Duration.between(this.start, this.finish);
		return d.toMillis();
	}

	public Long getComputationinSeconds() {
		Duration d = Duration.between(this.start, this.finish);
		return d.getSeconds();
	}

	@Override
	public String toString() {
		return Util.obj2json(this);
	}

}
