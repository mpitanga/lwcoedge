package br.edu.ufrj.lwcoedge.core.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Util {

	private static RestTemplate restTemplate = null;
	
/*	
	private static HttpComponentsClientHttpRequestFactory getHttpComponentsClientHttpRequestFactory(int timeout, int poolMax) {
	    PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
	    poolingConnectionManager.setMaxTotal(poolMax);
	    CloseableHttpClient client = HttpClientBuilder.create().setConnectionManager(poolingConnectionManager).build();
	    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
	        new HttpComponentsClientHttpRequestFactory(client);
	    clientHttpRequestFactory.setConnectTimeout(timeout);
	    clientHttpRequestFactory.setConnectionRequestTimeout(timeout);
	    return clientHttpRequestFactory;
	}

	
	public static RestTemplate getRestTemplate(int timeout) {
		if (restTemplate == null) {
			restTemplate = 
					new RestTemplate(
							getHttpComponentsClientHttpRequestFactory(timeout, 200)
					);			
		}
		((HttpComponentsClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(timeout);
		((HttpComponentsClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectionRequestTimeout(timeout);		
		return restTemplate;
	}
	
	//SimpleClientHttpRequestFactory
*/

	public static RestTemplate getRestTemplate(int timeout) {
		if (restTemplate == null) {
			restTemplate = 
					new RestTemplate(
							new SimpleClientHttpRequestFactory()
					);			
		}
		((SimpleClientHttpRequestFactory)restTemplate.getRequestFactory()).setConnectTimeout(timeout);
		return restTemplate;
	}

	public static HttpHeaders getDefaultHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return headers;
	}

	public static HttpHeaders getDefaultHeaders(LinkedHashMap<String, String> values) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		values.forEach((key, value) -> headers.add(key, value));
		return headers;
	}

	/**
	 * Sending a request
	 * @param URL
	 * @param headers
	 * @param method
	 * @param data
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public static <T> ResponseEntity<T> sendRequest(String URL, HttpHeaders headers, HttpMethod method, Object data, Class<T> response, Integer... args) /*throws Exception*/ {
		int timeout = (args.length==1) ? args[0] : 300;
		if (headers == null) {
			headers = getDefaultHeaders();
		}
		RestTemplate rt = getRestTemplate(timeout);
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(data, headers);
		ResponseEntity<T> resp = rt.exchange(URL, method, requestEntity, response);
		if (resp.getStatusCode() != HttpStatus.OK) {
			//throw new Exception ("Http Error! "+resp.getStatusCodeValue()+"\n"+resp.getStatusCode().getReasonPhrase());
			throw new HttpServerErrorException(resp.getStatusCode(),
					"Http Error! "+Integer.toString(resp.getStatusCodeValue())+"\n"+resp.getStatusCode().getReasonPhrase()
				);
		}
		return resp;
	}

	public static void callBack(String callBackURL, Object data) throws Exception {
		sendRequest(callBackURL, getDefaultHeaders(), HttpMethod.POST, data, Object.class);
	}

	/**
	 * 
	 * @param obj Object to determine the size.
	 * @return The size calculated is approximate.
	 */
	public static long getObjectSize(Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj).length();
		} catch (JsonProcessingException e) {
			return 0;
		}
	}
	/*
	 * Linux problem
	 * We are using the solution available on the site below to face the problem of the process deadlock 
	 * when it is started from another process. 
	 * 
	 * https://www.javaworld.com/article/2071275/core-java/when-runtime-exec---won-t.html?page=2
	 */
	public static void exec(String command, long miliseconds) throws IOException, InterruptedException {
		FileOutputStream fos = new FileOutputStream(Thread.currentThread().getName());

		Runtime r = Runtime.getRuntime();
		Process proc = null;
		if (System.getProperty("os.name").toLowerCase().contains("win")) {
			proc = r.exec( "cmd /c start /min /wait "+command );
		} else {
			proc = r.exec(Util.msg(command));
			// any error message?
	        StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");            
	        // any output? 
	        StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT", fos);
	        // kick them off
	        errorGobbler.start();
	        outputGobbler.start();
		}
		if (miliseconds>0){
			proc.waitFor(miliseconds, TimeUnit.MILLISECONDS);
		}
		fos.flush();
        fos.close();
	}
	
	private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
	
	public static double calcPercentage(long value1, long value2, int precision) {
		BigDecimal result = BigDecimal.valueOf(value2).divide(BigDecimal.valueOf(value1), 4, RoundingMode.HALF_UP);
		result = BigDecimal.ONE.subtract(result).multiply(ONE_HUNDRED).setScale(precision, RoundingMode.HALF_UP);
		return result.doubleValue();
	}

	public static String obj2json(Object obj){
		String json = null;
		if(obj!=null){
			try {
				ObjectMapper mapper = new ObjectMapper();
				json = mapper.writeValueAsString(obj);
			} catch (JsonProcessingException e) {
				json = e.getMessage();
			} 
		}
		return json;
	}
	
	public static <T> T json2obj(String jsonStr, Class<T> t ) throws Exception {
		T json = null;
		if(jsonStr!=null){
			try {
				ObjectMapper mapper = new ObjectMapper();
				json = mapper.readValue(jsonStr, t);
			} catch (IOException e) {
				throw e;
			}
		}
		return json;
	}

	public static String msg(String... strs) {
		StringBuilder sb = new StringBuilder();
		for (String str : strs) {
			sb.append(str);
		}
		return sb.toString();
	}
	
	public static String escape(String raw) {
		return StringEscapeUtils.escapeJava(raw);
	}

	public static LocalDateTime getStringToDateTime(String value, String format) throws ParseException {
		return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format));
	}

	public static LocalDate getStringToDate(String value, String format) throws ParseException {
		return LocalDate.parse(value, DateTimeFormatter.ofPattern(format));
	}

	public static String getDateTimeToString(LocalDateTime value, String format) throws ParseException {
		return value.format(DateTimeFormatter.ofPattern(format));
	}

}
