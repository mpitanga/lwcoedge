package br.edu.ufrj.lwcoedge.metrics.manager.service;

import java.lang.annotation.Native;
import java.util.ArrayList;

import org.apache.commons.collections.map.LRUMap;
import org.springframework.stereotype.Service;

import br.edu.ufrj.lwcoedge.core.cache.Cache;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.AbstractMetric;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmount;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmountAndTimes;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricAmountAndValue;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricCollected;
import br.edu.ufrj.lwcoedge.core.metrics.experiment.MetricComputationTime;
import br.edu.ufrj.lwcoedge.core.service.AbstractService;
import br.edu.ufrj.lwcoedge.core.util.Util;

@Service
public class ExperimentService extends AbstractService {

	// This constant defines the amount of metrics collected
	@Native private static int KEY_ELEMENTS = 200;
	@Native private static int SUMMARY_ELEMENTS  = 100;
	@Native private static int MAX_ELEMENTS  = 15000;
	@Native private static int TIMETOLIVE    = 3600 * 48;
	@Native private static int TIMEINTERVAL  = 3600 * 48;
	
	// Key - Value
    private  Cache<String, Cache<String, AbstractMetric>> cacheMetrics = new Cache<String, Cache<String, AbstractMetric>>(TIMETOLIVE, TIMEINTERVAL, KEY_ELEMENTS);

	private boolean active = true;

	public void setActive(boolean active) throws Exception  {
		this.active = active;
	}

	public boolean isActive() throws Exception  {
		return this.active;
	}
	
	public synchronized void put(MetricCollected m) throws Exception {
		if (m.getMetricAmount() != null) {
			this.putMetricAmount(m.getMetric(), m.getMetricAmount());
		}
		if (m.getMetricComputationalTime() != null) {		
			this.putMetricComputationalTime(m.getMetric(), m.getMetricComputationalTime());
		}
		if (m.getMetricAmountAndTimes() != null) {		
			this.putMetricAmountAndTimes(m.getMetric(), m.getMetricAmountAndTimes());
		}
		if (m.getMetricAmountAndValue() != null) {		
			this.putMetricAmountAndValue(m.getMetric(), m.getMetricAmountAndValue());
		}
		
	}

    private void putMetricAmount(String metric, MetricAmount ma) throws Exception {
    	if (!this.isActive()) return;
    	Cache<String, AbstractMetric> cache = cacheMetrics.get(metric);
    	if (cache == null) {
    		cache = new Cache<String, AbstractMetric>(TIMETOLIVE, TIMEINTERVAL, SUMMARY_ELEMENTS);
    	}
    	MetricAmount m = (MetricAmount)cache.get(ma.getId());  	
    	if (m==null) {
    		m = new MetricAmount(ma.getId());
    	}
    	Integer amountOf = m.getAmountOf() + ma.getAmountOf();
    	m.setAmountOf(amountOf);
    	cache.put(ma.getId(), m);
    	cacheMetrics.put(metric, cache);
    }
  
    private void putMetricComputationalTime(String metric, MetricComputationTime mct)  throws Exception {
    	if (!this.isActive()) return;
    	
    	this.getLogger().info( Util.msg("Registering metric [",metric,"]...") );    	
    	Cache<String, AbstractMetric> cache = cacheMetrics.get(metric);
    	this.getLogger().info( Util.msg("Metric in the cache ", Util.obj2json(cache)));
    	if (cache == null) {
    		cache = new Cache<String, AbstractMetric>(TIMETOLIVE, TIMEINTERVAL, MAX_ELEMENTS);
    	}
    	cache.put(mct.getId(), mct);
    	cacheMetrics.put(metric, cache);
    	this.getLogger().info( Util.msg("Metric after cache updated ", Util.obj2json(cache)));
    }

    private void putMetricAmountAndTimes(String metric, MetricAmountAndTimes maat)  throws Exception {
    	if (!this.isActive()) return;
    	Cache<String, AbstractMetric> cache = cacheMetrics.get(metric);
    	if (cache == null) {
    		cache = new Cache<String, AbstractMetric>(TIMETOLIVE, TIMEINTERVAL, SUMMARY_ELEMENTS);
    	}
    	MetricAmountAndTimes m = (MetricAmountAndTimes)cache.get(maat.getId());
    	if (m==null) {
    		m = new MetricAmountAndTimes();
    		m.setId(maat.getId()); 
    		m.setAmountOf(0);
    		m.setComputationinMillis(0l);
    		m.setComputationinSeconds(0l);
    	} 
       	m.setComputationinMillis( m.getComputationinMillis() + maat.getComputationinMillis());
       	m.setComputationinSeconds(m.getComputationinSeconds() + maat.getComputationinSeconds());
   		m.setAmountOf( m.getAmountOf() + maat.getAmountOf() );
       	cache.put(maat.getId(), m);
    	cacheMetrics.put(metric, cache);
    }

    private void putMetricAmountAndValue(String metric, MetricAmountAndValue maav)  throws Exception {
    	if (!this.isActive()) return;
    	Cache<String, AbstractMetric> cache = cacheMetrics.get(metric);
    	if (cache == null) {
    		cache = new Cache<String, AbstractMetric>(TIMETOLIVE, TIMEINTERVAL, SUMMARY_ELEMENTS);
    	}
    	MetricAmountAndValue m = (MetricAmountAndValue)cache.get(maav.getId());
    	if (m==null) {
    		m = new MetricAmountAndValue();
    		m.setId(maav.getId()); 
    		m.setAmountOf(0);
    		m.setValueOf(0l);
    	} 
       	m.setValueOf( m.getValueOf() + maav.getValueOf());
   		m.setAmountOf( m.getAmountOf() + maav.getAmountOf() );
       	cache.put(maav.getId(), m);
    	cacheMetrics.put(metric, cache);
    }

    public void remove(String metric, String key)  throws Exception {
    	Cache<String, AbstractMetric> cache = cacheMetrics.get(metric);
    	cache.remove(key);
    }

    public int size()  throws Exception {
    	return cacheMetrics.size();
    }

    public int size(String metric)  throws Exception {
    	return cacheMetrics.get(metric).size();
    }

    public LRUMap getAll()  throws Exception {
    	return cacheMetrics.get();
    }
    
    public ArrayList<AbstractMetric> getAll(String metric)  throws Exception {
    	return cacheMetrics.get(metric).getAll();
    }

    public void cleanup()  throws Exception {
    	cacheMetrics.cleanup();
    }

    public void clearAll()  throws Exception {
    	cacheMetrics.clearAll();
    }

	public Object getKeys()  throws Exception {
		return cacheMetrics.getKeys();
	}
}
