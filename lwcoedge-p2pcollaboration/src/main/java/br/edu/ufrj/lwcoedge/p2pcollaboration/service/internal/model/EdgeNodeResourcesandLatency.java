package br.edu.ufrj.lwcoedge.p2pcollaboration.service.internal.model;

import java.io.Serializable;

import br.edu.ufrj.lwcoedge.core.model.EdgeNode;
import br.edu.ufrj.lwcoedge.core.model.ResourcesAvailable;

public class EdgeNodeResourcesandLatency implements Serializable, Comparable<EdgeNodeResourcesandLatency> {

	private static final long serialVersionUID = 7568854647441316836L;

	EdgeNode edgenode;
	ResourcesAvailable resources;
	long latency;
	
	public EdgeNodeResourcesandLatency() {}

	public EdgeNodeResourcesandLatency(EdgeNode edgenode, ResourcesAvailable resources, long latency) {
		super();
		this.edgenode = edgenode;
		this.resources = resources;
		this.latency = latency;
	}

	/**
	 * @return the latency
	 */
	public long getLatency() {
		return latency;
	}

	/**
	 * @param latency the latency to set
	 */
	public void setLatency(long latency) {
		this.latency = latency;
	}

	/**
	 * @return the edgenode
	 */
	public EdgeNode getEdgenode() {
		return edgenode;
	}

	/**
	 * @param edgenode the edgenode to set
	 */
	public void setEdgenode(EdgeNode edgenode) {
		this.edgenode = edgenode;
	}

	/**
	 * @return the resources
	 */
	public ResourcesAvailable getResources() {
		return resources;
	}

	/**
	 * @param resources the resources to set
	 */
	public void setResources(ResourcesAvailable resources) {
		this.resources = resources;
	}
	
	@Override
	public int compareTo(EdgeNodeResourcesandLatency other) {
		int ordem1 = 0;
		int ordem2 = 0;
		long latDiff = this.getLatency() - other.getLatency();
		if (latDiff > 0) { // this.latency > other.latency
			ordem1 += 2;
		} else {
			if (latDiff < 0) {
				ordem2 += 2;
			}
		}

		if (this.getResources().getCpu() > other.getResources().getCpu()) {
			ordem2++;
		} else {
			if (this.getResources().getCpu() < other.getResources().getCpu()) {
				ordem1++;
			}
		}
		double memFree1 = this.getResources().memoryAvailable();
		double memFree2 = other.getResources().memoryAvailable();
		if (memFree1 > memFree2) {
			ordem2++;
		} else if (memFree1 < memFree2) {
			ordem1++;
		}
		
		if (ordem1 > ordem2) {
			return 1;
		} else {
			if (ordem1 < ordem2) {
				return -1;
			}
		}
		return 0;
	}

	@Override
	public String toString() {
		return this.edgenode.getHostName() + " Resources : [ CPU : "+ resources.getCpu() + " Mem available(%) : "+resources.memoryAvailable()+ " ] Latency : "+latency;
	}

/*
	public static void main(String[] args) {
		ArrayList<EdgeNodeResourcesandLatency> arrayRes = new ArrayList<EdgeNodeResourcesandLatency>();
		EdgeNodeResourcesandLatency res;
		
		EdgeNode en = new EdgeNode();
		en.setHostName("en1");		
		ResourcesAvailable m1 = new ResourcesAvailable();
		m1.setTotalPhysicalMemorySize(101888l);
		m1.setFreePhysicalMemorySize(90000l);
		m1.setCpu(2l);

		res = new EdgeNodeResourcesandLatency(en, m1, 41);
		arrayRes.add(res);

		en = new EdgeNode();
		en.setHostName("en2");
		m1 = new ResourcesAvailable();
		m1.setTotalPhysicalMemorySize(101888l);
		m1.setFreePhysicalMemorySize(91000l);
		m1.setCpu(2l);
		res = new EdgeNodeResourcesandLatency(en, m1, 43);
		arrayRes.add(res);

		en = new EdgeNode();
		en.setHostName("en3");
		m1 = new ResourcesAvailable();
		m1.setTotalPhysicalMemorySize(101888l);
		m1.setFreePhysicalMemorySize(91000l);
		m1.setCpu(2l);
		res = new EdgeNodeResourcesandLatency(en, m1, 41);
		arrayRes.add(res);

		Collections.sort(arrayRes);
		for (EdgeNodeResourcesandLatency enRes : arrayRes) {
			System.out.println(enRes.toString());
		}
	}
*/

}
