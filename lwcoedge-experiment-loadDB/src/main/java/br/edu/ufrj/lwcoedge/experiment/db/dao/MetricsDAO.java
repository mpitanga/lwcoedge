package br.edu.ufrj.lwcoedge.experiment.db.dao;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmount;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmountAndValue;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricIdentification;
import br.edu.ufrj.lwcoedge.experiment.core.model.MetricCT;
import br.edu.ufrj.lwcoedge.experiment.core.model.MetricCTAndAmount;
import br.edu.ufrj.lwcoedge.experiment.db.entities.Metricamount;
import br.edu.ufrj.lwcoedge.experiment.db.entities.Metricamountandtime;
import br.edu.ufrj.lwcoedge.experiment.db.entities.Metricamountandvalue;
import br.edu.ufrj.lwcoedge.experiment.db.entities.Metriccomputationtime;

@Repository
public class MetricsDAO {

	@Autowired
	private EntityManager em;
	
	public MetricsDAO() {}
	
	public void saveMetricAmount(String experimentName, String edgeNode, MetricAmount ma, int variation) throws Exception {
		Metricamount maDB = new Metricamount();
		MetricIdentification mi = new MetricIdentification(ma.getId());
		
		//E1.200
		String[] exp = mi.getExperiment().split("\\.");
		
		maDB.setExperimentname(experimentName);
		maDB.setExperimentcode(exp[0]);
		
		maDB.setEdgenode(edgeNode);
		maDB.setMetric(mi.getMetric());
		maDB.setVariation(variation);
		maDB.setDatatypeid(mi.getDatatypeID());

		maDB.setAmountof(ma.getAmountOf());
		em.merge(maDB);
	}

	public void saveMetricComputationTime(String experimentName, String edgeNode, MetricCTAndAmount mc, int variation) {
		Metricamountandtime maDB = new Metricamountandtime();
		MetricIdentification mi = new MetricIdentification(mc.getId());
		
		//E1.200
		String[] exp = mi.getExperiment().split("\\.");
		
		maDB.setExperimentname(experimentName);
		maDB.setExperimentcode(exp[0]);
		
		maDB.setEdgenode(edgeNode);
		maDB.setMetric(mi.getMetric());
		maDB.setVariation(variation);

		maDB.setDatatypeid(mi.getDatatypeID());

		maDB.setAmountof(mc.getAmountOf().intValue());
		maDB.setComputationinmillis(mc.getComputationinMillis().intValue());
		maDB.setComputationinseconds(mc.getComputationinSeconds().intValue());
		em.merge(maDB);
	}

	public void saveMetricAmountAndValue(String experimentName, String edgeNode, MetricAmountAndValue mc, int variation) {
		Metricamountandvalue maDB = new Metricamountandvalue();
		MetricIdentification mi = new MetricIdentification(mc.getId());
		
		//E1.200
		String[] exp = mi.getExperiment().split("\\.");
		
		maDB.setExperimentname(experimentName);
		maDB.setExperimentcode(exp[0]);
		
		maDB.setEdgenode(edgeNode);
		maDB.setMetric(mi.getMetric());
		maDB.setVariation(variation);
		
		maDB.setDatatypeid(mi.getDatatypeID());

		maDB.setAmountof(mc.getAmountOf().intValue());
		maDB.setValueof(mc.getValueOf().intValue());
		em.merge(maDB);
		
	}
	
	public void saveMetricComputationTimeValues(String experimentName, String edgeNode, MetricCT mc, int variation) {
		Metriccomputationtime maDB = new Metriccomputationtime();
		MetricIdentification mi = new MetricIdentification(mc.getId());
		
		//E1.200
		String[] exp = mi.getExperiment().split("\\.");
		
		maDB.setExperimentname(experimentName);
		maDB.setExperimentcode(exp[0]);
		
		maDB.setEdgenode(edgeNode);
		maDB.setMetric(mi.getMetric());
		maDB.setVariation(variation);
		
		maDB.setDatatypeid(mi.getDatatypeID());
		
		maDB.setStart( mc.getStart() );
		maDB.setFinish( mc.getFinish() );
		
		maDB.setComputationinmillis( mc.getComputationinMillis().intValue() );
		maDB.setComputationinseconds( mc.getComputationinSeconds().intValue() );

		em.merge(maDB);
		
	}

}
