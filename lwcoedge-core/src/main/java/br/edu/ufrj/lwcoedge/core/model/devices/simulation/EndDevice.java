package br.edu.ufrj.lwcoedge.core.model.devices.simulation;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.edu.ufrj.lwcoedge.context.broker.OrionBroker;
import br.edu.ufrj.lwcoedge.context.broker.elements.Attributes;
import br.edu.ufrj.lwcoedge.context.broker.elements.ContextBrokerResponse;
import br.edu.ufrj.lwcoedge.context.broker.elements.ContextElement;
import br.edu.ufrj.lwcoedge.context.broker.elements.Metadata;
import br.edu.ufrj.lwcoedge.core.model.Data;
import br.edu.ufrj.lwcoedge.core.util.Util;

public class EndDevice extends AbstractDevice implements Serializable {

	private static final long serialVersionUID = 8866494361321407243L;

	private Logger logger = LogManager.getLogger(getClass());
	//Logger.getLogger(this.getClass().getName());
	
	private EndDeviceType type;
	private Data data;
	
	private OrionBroker ob;

	public EndDevice(String hostName) {
		super(hostName);
		this.generateRandomData();		
		this.ob = new OrionBroker("broker","1026","lwcoedge", "/");
	}

	private Integer getRamdomValue() {
		Random r = new Random();
		return r.nextInt(23)+25;
	}

	private void generateRandomData() {
		Data d = new Data();
		d.setValue(getRamdomValue());
		String dh = LocalDateTime.now()
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		d.setAcquisitiondatetime(dh);
		this.data = d;
	}
	
	// Direct access
	public Data getData() {
		try {
			Thread.sleep(38);
			this.generateRandomData();
			return this.data;
		} catch (InterruptedException e) {
			logger.error(e.getMessage());
			return this.data;
		}
	}

	public Data getDataDB() {
		Data d = new Data();
		try {
//			this.generateRandomData();
			// The communication latency is based on the access to the FIWARE Orion Broker component.
			// this number is an average measured after 200 access.
			// timeinstant = 2019-06-06T20:24:36.803Z
			
			ContextBrokerResponse response = ob.getEntityById(this.getHostName(), "Device");
			ContextElement element = response.getContextResponses().get(0).getContextElement();
			Attributes attr = element.getAttributes().get(4);
			d.setValue(attr.getValue());
			String dh;
			
			if (attr.getMetadatas().size()>0) {
				Metadata meta = attr.getMetadatas().get(0);
				dh = meta.getValue().replace('T', ' ').replaceAll("Z", "");
			} else {
				attr = element.getAttributes().get(0);
				dh = attr.getValue().replace('T', ' ').replaceAll("Z", "");				
			}

			d.setAcquisitiondatetime(dh);
			this.data = d;

			return data;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return data;
		}
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Data data) {
		this.data = data;
	}

	@Override
	public Float getEnergy() {
		return this.getWatts();
	}

	/**
	 * @return the type
	 */
	public EndDeviceType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(EndDeviceType type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}

}
