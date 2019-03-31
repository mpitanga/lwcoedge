package br.edu.ufrj.lwcoedge.core.model.devices.simulation;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.logging.Logger;

import br.edu.ufrj.lwcoedge.core.model.Data;
import br.edu.ufrj.lwcoedge.core.util.Util;

public class EndDevice extends AbstractDevice implements Serializable {

	private static final long serialVersionUID = 8866494361321407243L;

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private EndDeviceType type;
	private Data data;
	private Long interval = Long.valueOf(30 * 1000); // 30s
	
	public EndDevice(String hostName) {
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
			Thread.sleep(30);
			this.generateRandomData();
			return this.data;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return this.data;
		}
	}

	public Data getDataDB() {
		try {
//			this.generateRandomData();
			// The communication latency is based on the access to the FIWARE Orion Broker component.
			Thread.sleep(97); 
			return data;
		} catch (InterruptedException e) {
			e.printStackTrace();
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
		new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				this.generateRandomData();
				this.logger.info("Sending data ["+data.toString()+" to the internal database...");
				try {
					Thread.sleep(this.interval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}			
			}			
		}).start();
	}

/*
	public static void main(String[] args) {
		EndDevice ed = new EndDevice("DeviceID1");
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
*/
	
}
