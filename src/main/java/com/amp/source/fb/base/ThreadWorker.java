/**
 * 
 */
package com.amp.source.fb.base;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.Assert;
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

import com.amp.common.api.impl.ToolkitConstants;
import com.amp.jpa.entities.ThreadConfiguration;
import com.amp.jpa.entities.WorkerData;
import com.amp.jpaentities.mo.WorkerDataListMO;
import com.amp.jpaentities.mo.WorkerDataMO;
import com.amp.jpaentities.mo.WorkerThreadMO;
import com.amp.service.rest.model.WorkerDataRequestRO;


/**
 * @author MVEKSLER
 *
 */
public abstract class ThreadWorker 
{
	private static final Logger LOG = 
			LoggerFactory.getLogger(ThreadWorker.class);
	
	private URI getDataBaseURI()
	{
		try
		{
			URI uri = new URI(this.wkDataServiceURI);
			
			return uri;
		} 
		catch (URISyntaxException e) 
		{
			LOG.error("::Exception:" + e.getMessage(), e);
			
			return null;
		}
	}
	
	private URI getStatusBaseURI()
	{
		try
		{
			URI uri = new URI(this.wkStatusServiceURI);
			
			return uri;
		} 
		catch (URISyntaxException e) 
		{
			LOG.error("::Exception:" + e.getMessage(), e);
			
			return null;
		}
	}
	
	protected boolean wkIsRunThread = true;
	
	protected String wkStatusServiceURI = "";
					
	protected String wkDataServiceURI = "";
	
	protected WorkerThreadMO cWorkerThreadMO = 
			new WorkerThreadMO();
	
	protected HashMap<String, ThreadConfiguration> 
		cThreadConfiguration = 
			new HashMap<String, ThreadConfiguration>();
	
	protected HashMap<String, String> 
		cThreadConfigurationMap = 
			new HashMap<String, String>();
	
	protected HashMap<String, String> 
		cSystemConfiguration = 
			new  HashMap<String, String>();
	
	//---getters/setters
	public HashMap<String, String> getcSystemConfiguration() {
		return cSystemConfiguration;
	}

	public void setcSystemConfiguration(HashMap<String, String> cSystemConfiguration) {
		this.cSystemConfiguration = cSystemConfiguration;
	}

	public WorkerThreadMO getcWorkerThreadMO() {
		return cWorkerThreadMO;
	}

	public void setcWorkerThreadMO(WorkerThreadMO cWorkerThreadMO) {
		this.cWorkerThreadMO = cWorkerThreadMO;
	}

	public HashMap<String, ThreadConfiguration> getcThreadConfiguration() {
		return cThreadConfiguration;
	}

	public void setcThreadConfiguration(HashMap<String, ThreadConfiguration> cThreadConfiguration) {
		this.cThreadConfiguration = cThreadConfiguration;
	}

	public HashMap<String, String> getcThreadConfigurationMap() {
		return cThreadConfigurationMap;
	}

	public void setcThreadConfigurationMap(HashMap<String, String> cThreadConfigurationMap) {
		this.cThreadConfigurationMap = cThreadConfigurationMap;
	}

	public boolean isWkIsRunThread() {
		return wkIsRunThread;
	}

	public void setWkIsRunThread(boolean wkIsRunThread) {
		this.wkIsRunThread = wkIsRunThread;
	}

	public String getWkStatusServiceURI() {
		return wkStatusServiceURI;
	}

	public void setWkStatusServiceURI(String wkStatusServiceURI) {
		this.wkStatusServiceURI = wkStatusServiceURI;
	}

	public String getWkDataServiceURI() {
		return wkDataServiceURI;
	}

	public void setWkDataServiceURI(String wkDataServiceURI) {
		this.wkDataServiceURI = wkDataServiceURI;
	}
	
	public ThreadWorker()
	{
		
	}
	
	protected boolean init()
	{
		boolean cRes = true;
		
		String cMethodName = "";
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        
	        cMethodName = ste.getMethodName();
	        
	        this.cThreadConfiguration = new HashMap<String, ThreadConfiguration>();
	        
	        this.cThreadConfigurationMap = new  HashMap<String, String>();
	        
	        this.cSystemConfiguration = new  HashMap<String, String>();

	        return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return cRes;
		}
	}
	//---
	protected boolean saveWorkerData() 
	{
		String cMethodName = "";

		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	       
			return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return cRes;
		}
	}
	
	//---
	protected WorkerDataListMO getItemOpStatus(String sourceName,
											   String targetName,
											   String workerName,
											   String threadName,
											   String itemKey, 
										       String opTypeName) 
	{
		String cMethodName = "";
		
		WorkerDataListMO cWorkerDataListMO = new WorkerDataListMO();
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
			
	        //---prepare error response
	        WorkerData cWorkerDataErr = new WorkerData();
	        cWorkerDataErr.setUpdatedate(Calendar.getInstance().getTime());
	        cWorkerDataErr.setItemid(ToolkitConstants.ERROR_STR);
			
			WorkerDataMO cWorkerDataMOErr = new WorkerDataMO();
			cWorkerDataMOErr.setcWorkerData(cWorkerDataErr);
			
			cWorkerDataListMO.cWorkerData.add(cWorkerDataMOErr);
			//---
			WorkerDataRequestRO requestObject = new WorkerDataRequestRO();
		    requestObject.setSourceName(sourceName);
		    requestObject.setTargetName(targetName);
		    requestObject.setWorkerName(workerName);
		    requestObject.setThreadName(threadName);
		    requestObject.setOpTypeName(opTypeName);
		    requestObject.setItemKey(itemKey);
		    
		    HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		    
		    ClientHttpRequestFactory requestFactory = 
					new HttpComponentsClientHttpRequestFactory();
	        
	        RestTemplate restTemplate = new RestTemplate(requestFactory);
	        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	        
	        URI uri = this.getDataBaseURI();
	        if ( null == uri )
	        {
	        	LOG.info(cMethodName + "::uri is null: ");
	        	
	        	return cWorkerDataListMO;
	        }
	        
		    final String endpointURL = uri.toString() + "/getItemOpStatus";
		    
		    LOG.info(cMethodName + "::formattedURL: " + endpointURL);
	        
		    HttpEntity<?> requestEntity = new HttpEntity<WorkerDataRequestRO>(requestObject, headers);
		   
		    ResponseEntity<WorkerDataListMO> response = restTemplate.exchange(
		    		endpointURL, HttpMethod.POST, requestEntity, WorkerDataListMO.class);
	        
			Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
	
			cWorkerDataListMO = response.getBody();
			
			for ( WorkerDataMO cWorkerDataMO : cWorkerDataListMO.cWorkerData)
			{
				WorkerData cWorkerData = cWorkerDataMO.cWorkerData;
				
				LOG.info(cMethodName + "::" + 
						cWorkerData.getItemid() + ":" +
						cWorkerData.getOperationtypeM().getName() + ":" +
						cWorkerData.getUpdatedate().toString());
			}
			
			return cWorkerDataListMO;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
	
			return cWorkerDataListMO;
		}
	}

	//---
	protected boolean setItemOpStatus(
			String itemId, 
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
	        
	        /*
	        String cSourceName = "";
	        ThreadConfiguration cThreadConfig = this.cThreadConfiguration.get("wkGroupId");
	        if ( cThreadConfig != null )
	        {
	        	cSourceName = cThreadConfig.getConfigvalue();
	        }
	        */
	        
	        
	        String workerName  = this.getcWorkerThreadMO().getcWorker().getName();
	        String threadName  = this.getcWorkerThreadMO().getcThread().getName();
	        String sourceName  = "NA";
	        String targetName  = "NA";
	        
	        HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		    
		    ClientHttpRequestFactory requestFactory = 
					new HttpComponentsClientHttpRequestFactory();
	        
	        RestTemplate restTemplate = new RestTemplate(requestFactory);
	        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	        		   
	        URI uri = this.getStatusBaseURI();
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
			
			return false;
		}
	}
}
