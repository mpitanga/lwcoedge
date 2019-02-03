package br.edu.ufrj.lwcoedge.core.service;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.springframework.boot.ApplicationArguments;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ufrj.lwcoedge.core.model.ComponentsPort;

public abstract class AbstractService {

	private String name = this.getClass().getSimpleName();
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private String hostName = "localhost";
	private ComponentsPort ports;
	
	public AbstractService() {
		InetAddress addr;
		try {
			addr = InetAddress.getLocalHost();
			this.hostName = addr.getHostName();			
		} catch (UnknownHostException e) {
		}
	}
		
	public ComponentsPort getPorts() {
		return ports;
	}

	public void setPorts(ComponentsPort ports) {
		this.ports = ports;
	}

	public String getHostName() {
		return hostName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return logger;
	}

	protected void loadComponentsPort(ApplicationArguments args) throws Exception {
		this.getLogger().info("Loading the LW-CoEdge components port settings...");
		String fileName = args.getOptionValues("PortsConfig").get(0);
		ObjectMapper objectMapper = new ObjectMapper();
		this.ports = objectMapper.readValue(new File(fileName), ComponentsPort.class);
		this.getLogger().info(this.ports.toString());
		this.getLogger().info("LW-CoEdge components port settings loaded.");
	}

	protected String getUrl(String protocol, String host, Integer port, String app) {
		StringBuilder url = new StringBuilder();
		url.append(protocol);
		url.append(host).append(":");
		url.append(port.toString());
		if (!app.trim().isEmpty()) {
			url.append(app);
		}
		return url.toString();
	}
}
