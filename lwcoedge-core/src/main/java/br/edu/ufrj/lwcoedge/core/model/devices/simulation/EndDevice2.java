package br.edu.ufrj.lwcoedge.core.model.devices.simulation;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.edu.ufrj.lwcoedge.core.model.Data;
import br.edu.ufrj.lwcoedge.core.util.Util;

public class EndDevice2 extends AbstractDevice implements Serializable {

	private static final long serialVersionUID = 8866494361321407243L;

	private Logger logger = LogManager.getLogger(getClass());
	//Logger.getLogger(this.getClass().getName());
	
	private EndDeviceType type;
	private Data data;
	private Long interval = Long.valueOf(30 * 1000); // 30s
	
	public EndDevice2(String hostName) {
		super(hostName);
		this.run();
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

	public void setInterval(Long interval) {
		// To avoid blocking other threads
		this.interval = (interval == 0 ? 1000 : interval);
	}

	public Long getInterval() {
		return interval;
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
		try {
//			this.generateRandomData();
			// The communication latency is based on the access to the FIWARE Orion Broker component.
			// this number is an average measured after 200 access.
			Thread.sleep(19); 
			return data;
		} catch (InterruptedException e) {
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

	//Simulates the device sending his sensing data to the internal database..
	private void run() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					generateRandomData();
					logger.debug("Sending data [{} to the internal database...",data.toString());
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}			
				}				
			}
		});
		t.setDaemon(true);
		t.start();
	}

}
