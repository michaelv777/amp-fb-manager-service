package com.amp.source.fb.base;

import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.amp.service.rest.model.WorkerDataRequestRO;

public abstract class SourceManagerStorage 
{
	private static final Logger LOG = 
			LoggerFactory.getLogger(SourceManagerBase.class);
	
	protected String wkmStatusServiceURI = "";
				
	public String getWkmStatusServiceURI() {
		return wkmStatusServiceURI;
	}

	public void setWkmStatusServiceURI(String wkmStatusServiceURI) {
		this.wkmStatusServiceURI = wkmStatusServiceURI;
	}
	
	private URI getStatusServiceURI()
	{
		try
		{
			URI uri = new URI(this.wkmStatusServiceURI);
			
			return uri;
		} 
		catch (URISyntaxException e) 
		{
			LOG.error("::Exception:" + e.getMessage(), e);
			
			return null;
		}
	}
	
	//---
	public boolean setSourceItemStatus(String itemId, 
									   String sourceName,
									   String targetName,
									   String workerName,
									   String threadName,
									   String description, 
									   String status,
									   String opTypeName) 
	{
		String cMethodName = "";
		
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		    
		    ClientHttpRequestFactory requestFactory = 
					new HttpComponentsClientHttpRequestFactory();
	        
	        RestTemplate restTemplate = new RestTemplate(requestFactory);
	        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	        		   
	        URI uri = this.getStatusServiceURI();
	        if ( null == uri )
	        {
	        	LOG.info(cMethodName + "::uri is null: ");
	        	
	        	return false;
	        }
	        
		    final String endpointURL = uri.toString() + "/setItemOpStatus";
		    
		    LOG.info(cMethodName + "::formattedURL: " + endpointURL);
	        
		    WorkerDataRequestRO requestObject = new WorkerDataRequestRO();
		    requestObject.setSourceName(sourceName);
		    requestObject.setTargetName(targetName);
		    requestObject.setWorkerName(workerName);
		    requestObject.setThreadName(threadName);
		    requestObject.setOpTypeName(opTypeName);
		    requestObject.setItemKey(itemId);
		    requestObject.setDescription(description);
		    requestObject.setStatus(status);
		    
		    HttpEntity<?> requestEntity = new HttpEntity<WorkerDataRequestRO>(requestObject, headers);
		   
		    ResponseEntity<String> response = restTemplate.exchange(
		    		endpointURL, HttpMethod.POST, requestEntity, String.class);
			
			String cResponse =  String.valueOf(response.getStatusCode().value()) + ":" + response.getBody();
			
			if (response.getStatusCode() == HttpStatus.OK) 
			{
				cResponse =  String.valueOf(response.getStatusCode().value()) + ":" + response.getBody();
	
				LOG.info(cMethodName + "::" + cResponse);
			} 
			else 
			{
				cResponse = String.valueOf(response.getStatusCode().value()) + ":" + response.getBody();
	
				LOG.error(cMethodName + "::" + cResponse);
			}
	        
			return cRes;
    		
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return ( cRes = false );
		}
	}
	
	protected HashMap<String, Field> getInheritedFields(Class<?> type) 
	{
		String cMethodName = "";
		
		boolean cRes = true;
		
		HashMap<String, Field> cFields = new HashMap<String, Field>();
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        if ( type == null )
	        {
	        	cRes = false;
	        }
	        
	        if ( cRes )
	        {
		        for (Class<?> c = type; c != null; c = c.getSuperclass())
		        {
		        	for( Field cField : c.getDeclaredFields() )
		        	{
		        		cFields.put(cField.getName(), cField);
		        	}
		        }
	        }
	        
	        return cFields;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return new HashMap<String, Field>();
		}
    }
}
