package br.com.context.broker;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import br.com.context.broker.elements.ContextBrokerResponse;
import br.com.context.broker.elements.Entity;
import br.com.context.broker.elements.EntityObject;
import br.com.context.broker.elements.Payload;

public class OrionBroker {

	private String host;
	private String port;
	private String fiware_service;
	private String fiware_service_path;

	/**
	 * @param host The hostname or IP where the Context Broker is running.
	 * @param port The service port.
	 * @param fiware_service The string that identifies a FIWARE service, for instance, 'lw_cos'.
	 * @param fiware_service_path The string that identifies a FIWARE service path, for instance '/'.
	 */
	public OrionBroker(String host, String port, String fiware_service, String fiware_service_path) {
		super();
		this.host = host;
		this.port = port;
		this.fiware_service = fiware_service;
		this.fiware_service_path = fiware_service_path;
	}
	
	/**
	 * The private method in charge of assembling the Context Broker URL.
	 * @return The Context Broker URL.
	 */
	private String getBrokerContextUrl() {
		StringBuilder url = new StringBuilder();
		url.append("http://").append(this.host).append(":").append(this.port);
		return url.toString();
	}
	
	/**
	 * The private method in charge of assembling the default HTTP headers.
	 * @return The default HTTP headers.
	 */
	private HttpHeaders getDefaultHeaders() {
		HttpHeaders headers = new HttpHeaders();		
    	headers.add("Fiware-Service", fiware_service);
    	headers.add("Fiware-ServicePath", fiware_service_path);
    	headers.add("Accept", "application/json");
    	headers.add("Content-Type", "application/json; charset=utf-8");    	
    	return headers;	
	}
	
	/**
	 * The method is responsible for providing a unique identification of a document.
	 * @param entity_type Type of the document.
	 * @return It is the unique identification.
	 */
	public Long getNumber(String entity_type) {
		
		StringBuilder url = new StringBuilder();
		//'/v2/entities?type='+entity_type+'&limit=1&options=count'
		url.append(this.getBrokerContextUrl()).append("/v2/entities?type=").append(entity_type).append("&limit=1&options=count");
	
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter()); 
		
		HttpHeaders headers = this.getDefaultHeaders();
		HttpEntity<?> requestEntity = new HttpEntity<String>(headers);
		
		ResponseEntity<String> response =
		        restTemplate.exchange(url.toString(), HttpMethod.GET, requestEntity, String.class);

		Long id = (Long.parseLong(response.getHeaders().get("Fiware-Total-Count").get(0)))+1;

		return id;
	}
	
	/**
	 * The method is responsible for sending context information to store in the Broker.
	 * @param entity The context information.
	 * @return The result of processing.
	 * @throws Exception The error message.
	 */
	public ContextBrokerResponse updateContext(EntityObject entity) throws Exception {

		StringBuilder url = new StringBuilder();
		//'/v1/updateContext'
		url.append(this.getBrokerContextUrl()).append("/v1/updateContext");
		
		HttpHeaders headers = this.getDefaultHeaders();
		HttpEntity<EntityObject> requestEntity = new HttpEntity<EntityObject>(entity, headers);

		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<ContextBrokerResponse> response =
		        restTemplate.exchange(url.toString(), HttpMethod.POST, requestEntity, ContextBrokerResponse.class);
		return response.getBody();
	}

	private Payload getPayload(String id, String type, String isPattern) {
        Payload payload = new Payload();
        payload.getEntities().add(new Entity(id, type, isPattern));
		return payload;
	}
	
	public ContextBrokerResponse getEntityById(String id, String type) throws Exception {
		StringBuilder url = new StringBuilder();
		url.append(this.getBrokerContextUrl()).append("/ngsi10/queryContext");
		Payload payload = getPayload(id, type, "false");
		HttpHeaders headers = this.getDefaultHeaders();
		HttpEntity<Payload> requestEntity = new HttpEntity<Payload>(payload, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<ContextBrokerResponse> response =
		        restTemplate.exchange(url.toString(), HttpMethod.POST, requestEntity, ContextBrokerResponse.class);
		return response.getBody();
	}

	public ContextBrokerResponse getEntityByType(String type) throws Exception {
		StringBuilder url = new StringBuilder();
		url.append(this.getBrokerContextUrl()).append("/ngsi10/queryContext");
		if (type.trim().toUpperCase().equals("ALL")) {
			type = "";
		}
				
		Payload payload = getPayload(".*", type, "true");
		HttpHeaders headers = this.getDefaultHeaders();
		HttpEntity<Payload> requestEntity = new HttpEntity<Payload>(payload, headers);
		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<ContextBrokerResponse> response =
		        restTemplate.exchange(url.toString(), HttpMethod.POST, requestEntity, ContextBrokerResponse.class);
		return response.getBody();
	}

}
