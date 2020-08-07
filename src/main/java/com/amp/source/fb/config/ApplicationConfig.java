package com.amp.source.fb.config;

import java.util.Set;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@ComponentScan(basePackages = {"com.amp.source.fb.controller"})
@PropertySource("classpath:" + ApplicationConstants.PROPERTY_FILE_NAME)
public class ApplicationConfig 
{	
	private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfig.class);
	
	@Value("${http.client.read.timeout:5000}")
    private int readTimeout;

    @Value("${http.client.connect.timeout:300}")
    private int connectTimeout;
    
    @Value("${http.client.request.timeout:5000}")
    private int requestTimeout;
    
    @Value("${http.client.download.request.timeout:60000}")
    private int downloadRequestTimeout;
    
    @Value("${http.client.max.connections.total:100}")
    private int maxConnectionsTotal;
    
    @Value("${http.client.max.connections.per.host:100}")
    private int maxConnectionsPerRoute;
	
	@Bean
	@Primary
	public RestTemplate getRestTemplate(ObjectMapper objectMapper)
	{
		LOG.info("Initializing primary RestTemplate with "
				+ "maxConnections: {} "
				+ "maxConnectionsPerRoute: {} "
				+ "connectTimeout: {} "
				+ "connectionRequestTimeout: {} "
				+ "readTimeout: {}", 
				this.maxConnectionsTotal, 
				this.maxConnectionsPerRoute, 
				this.connectTimeout, 
				this.requestTimeout, 
				this.readTimeout);
		
		RestTemplate rest = new RestTemplate();
		
		HttpComponentsClientHttpRequestFactory requestFactory = 
				new HttpComponentsClientHttpRequestFactory(
						createHttpClient(
								this.maxConnectionsTotal, 
								this.maxConnectionsPerRoute, 
								this.readTimeout, 
								this.connectTimeout, 
								this.requestTimeout));
		
		rest.setRequestFactory(requestFactory);
		
		return rest;
	}
	
	 private CloseableHttpClient createHttpClient(
			 int maxConns, 
			 int maxConnPerRoute, 
			 int socketTimeout, 
			 int connTimeout, 
			 int reqConnTimeout) 
	 {
		 HttpClientBuilder builder = HttpClients
				.custom()
				.useSystemProperties()
				.disableCookieManagement();
		
		 if (maxConns >= 0) {
			builder.setMaxConnTotal(maxConns);
		 }
		
		 if (maxConnPerRoute >= 0) {
			builder.setMaxConnPerRoute(maxConnPerRoute);
		 }
		
		 Builder customReqConfig = RequestConfig.custom();
		 if (socketTimeout >= 0) {
			customReqConfig.setSocketTimeout(socketTimeout);
		 }
		
		 if (connTimeout >= 0) {
			customReqConfig.setConnectTimeout(connTimeout);
		 }
		
		 if (reqConnTimeout >= 0) {
			customReqConfig.setConnectionRequestTimeout(reqConnTimeout);
		 }

		 builder.setDefaultRequestConfig(customReqConfig.build());		
		
		 return builder.build();
	 }
	 
	 public Set<Class<?>> getClasses() {
	        return getRestClasses();
	 }
	    
	 //Auto-generated from RESTful web service wizard
	 private Set<Class<?>> getRestClasses() 
	 {
		Set<Class<?>> resources = new java.util.HashSet<Class<?>>();
		
		resources.add(com.amp.jpa.entities.WorkerConfiguration.class);
		
		return resources;    
	 }
}
