package com.amp.source.fb.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amp.common.api.impl.ToolkitConstants;
import com.amp.jpa.entities.ThreadConfiguration;
import com.amp.jpaentities.mo.ThreadMO;
import com.amp.jpaentities.mo.WorkerThreadMO;
import com.amp.source.fb.base.SourceManagerBase;
import com.amp.source.fb.thread.ManagedThreadFactoryImpl;
import com.amp.source.fb.worker.FacebookWorker;

/**
 * Service implementation class FacebookWorkerBean
 */

@Service("FacebookManagerBean")
public class FacebookManagerBean extends SourceManagerBase 
{
	private static final Logger LOG = 
			LoggerFactory.getLogger(FacebookManagerBean.class);
	
	//---class variables
	/*
	@Resource
	protected TimerService cTimerService = null;
	protected Timer cIntervalTimer = null; 
	*/
	
	//---getters/setters

	/*
	 * public TimerService getcTimerService() { return cTimerService; }
	 * 
	 * public void setcTimerService(TimerService cTimerService) { this.cTimerService
	 * = cTimerService; }
	 * 
	 * public Timer getcIntervalTimer() { return cIntervalTimer; }
	 * 
	 * public void setcIntervalTimer(Timer cIntervalTimer) { this.cIntervalTimer =
	 * cIntervalTimer; }
	 */
	
	//---class methods
    public FacebookManagerBean() 
    {
		String cMethodName = "";
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
		
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
		}
    }
    
	public FacebookManagerBean(String contextPath) 
	{
		String cMethodName = "";
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        this.contextPath = contextPath;
		
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
		}
	}

	@PostConstruct
	public boolean init()
	{
		boolean cRes = true;
		
		String cMethodName = "";
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        if ( cRes )
	        {
	        	cRes = super.init();
	        }
	        
	        if ( cRes )
	        {
	        	cRes = this.handleWorkersConfig();
	        }
	        
	        if ( cRes )
	        {
	        	cRes = this.initThreadsPool();
	        }
	        
	        /*
	        if ( cRes )
	        {
	        	cRes = this.initWorkersThreads();
	        }
	        */
	        
	        return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return false;
		}
	}

	public boolean start() 
	{
		boolean cRes = true;
		
		String cMethodName = "";
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	       
	        if ( cRes )
	        {
	        	cRes = this.initWorkersThreads();
	        }
	        
	        return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return false;
		}
	}
	/*
	public void initJUnit()
	{
		boolean cRes = true;
		
		String cMethodName = "";
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        if ( cRes )
	        {
	        	cRes = super.init();
	        }
	        if ( cRes )
	        {
	        	cRes = this.handleWorkersConfig();
	        }
	        if ( cRes )
	        {
	        	cRes = this.initThreadsPoolJUnit();
	        }
	        if ( cRes )
	        {
	        	cRes = this.initWorkersThreadsJUnit();
	        }
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
		}
	}
	*/
	
	/**
	 * @param cRes
	 * @param cClassName
	 * @return
	 */
	protected boolean handleWorkersConfig() 
	{
		boolean cRes = true;
		
		String cMethodName = "";
		@SuppressWarnings("unused")
		String cClassName =  "";
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        cClassName  = this.getClass().getSimpleName();
	        
	        String cSourceWorker = ToolkitConstants.AMP_FACEBOOK_SOURCE_WORKER;
	        
	        if ( cRes )
			{
				cRes = this.getWorker(cSourceWorker);
			}
	        if ( cRes )
			{
				cRes = this.setWorkerConfiguration();
			}
			if ( cRes )
			{
				cRes = this.setWorkerThreadsConfiguration();
			}
			if ( cRes )
			{
			    cRes = this.setWorkerThreads();
			}
			if ( cRes )
			{
			    cRes = this.setManagerProperties();
			}
			if ( cRes )
			{
			    cRes = this.setSystemProperties();
			}
			
			return cRes;
			
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return cRes;
		}
	}

	protected boolean initThreadsPool()
	{
		boolean cRes = true;
		
		String  cMethodName = "";
	
		try
    	{
    		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        if ( cRes )
	        {
		        //---init threads factory
		        if ( null == this.cThreadFactory )
		        {
		        	this.cThreadFactory = new ManagedThreadFactoryImpl();
		        }
	        }
	        
			/*
			 * if ( cRes ) { //---init monitoring timer TimerConfig timerConfig = new
			 * TimerConfig(); timerConfig.setPersistent(false);
			 * timerConfig.setInfo(cClassName);
			 * 
			 * this.cIntervalTimer = this.cTimerService.createIntervalTimer( 0,
			 * this.wkmKeepAliveTimer, timerConfig); }
			 */
	        
	        if ( cRes )
	        {
				//---init Executor Service
				if ( this.cWorkerThreads != null && this.cWorkerThreads.size() > 0 )
				{
					int cThreadsSize = this.cWorkerThreads.size();
					
					this.cThreadPoolExecutor = new ThreadPoolExecutor(
							cThreadsSize, 
							cThreadsSize,    
							this.wkmKeepAliveThread, 
							TimeUnit.MILLISECONDS,
							new LinkedBlockingQueue<Runnable>(cThreadsSize), 
							this.cThreadFactory); 
				}
	        }
	        
    		return cRes;
    	}
    	catch( Exception e)
    	{
    		LOG.error(cMethodName + "::" + e.getMessage(), e);
    		
    		return cRes;
    	}
	}
	
	/*
	protected boolean initThreadsPoolJUnit()
	{
		boolean cRes = true;
		
		String  cMethodName = "";
	
		try
    	{
    		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        if ( cRes )
	        {
		        //---init threads factory
		        if ( null == this.cThreadFactory )
		        {
		        	this.cThreadFactory = new ManagedThreadFactoryImpl();
		        }
	        }
	        
	        if ( cRes )
	        {
				//---init Executor Service
				if ( this.cWorkerThreads != null && this.cWorkerThreads.size() > 0 )
				{
					int cThreadsSize = this.cWorkerThreads.size();
					
					this.cThreadPoolExecutor = new ThreadPoolExecutor(
							cThreadsSize, 
							cThreadsSize,    
							this.wkmKeepAliveThread, 
							TimeUnit.MILLISECONDS,
							new LinkedBlockingQueue<Runnable>(cThreadsSize), 
							this.cThreadFactory); 
				}
	        }
	        
    		return cRes;
    	}
    	catch( Exception e)
    	{
    		LOG.error(cMethodName + "::" + e.getMessage(), e);
    		
    		return cRes;
    	}
	}
	*/
	
	/**
	 * 
	 */
	protected boolean initWorkersThreads() 
	{
		String cMethodName = "";
		
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        if ( this.cWorkerThreads != null && this.cThreadPoolExecutor != null)
	        {
	        	for( Map.Entry<ThreadMO, HashMap<String, ThreadConfiguration>> 
	        				cThreadConfigEntry : this.cWorkerThreads.entrySet() )
	        	{
	        		ThreadMO cThreadMO = cThreadConfigEntry.getKey();
	        		
	        		HashMap<String, ThreadConfiguration> cThreadConfig = 
	        				cThreadConfigEntry.getValue();
	        		
	        		WorkerThreadMO cWorkerThreadMO = 
	        				new WorkerThreadMO(cThreadMO.getcThread(), this.getcWorker());
	        		
	        		FacebookWorker cWorker = new FacebookWorker(
	        				cWorkerThreadMO, 
	        				cThreadConfig, 
	        				this.getcSystemConfig());
	        		
	        		/*
	        		FacebookWorker cWorker = new FacebookWorker(
	        				cThreadConfigMO, 
	        				cThreadConfig);
	        		
	        		cWorker.setcSystemConfiguration(this.getcSystemConfig());
	        		*/
	        		
	        		this.cThreadPoolExecutor.execute(cWorker);
	        	}
	        	
	        	this.cThreadPoolExecutor.shutdown();
	        }
	        
	        return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return cRes;
		}
	}

	/*
	protected boolean initWorkersThreadsJUnit() 
	{
		String cMethodName = "";
		
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        if ( this.cWorkerThreads != null && this.cThreadPoolExecutor != null)
	        {
	        	for( Map.Entry<ThreadMO, HashMap<String, ThreadConfiguration>> 
				cThreadConfigEntry : this.cWorkerThreads.entrySet() )
				{
	        		
					ThreadMO cThreadMO = cThreadConfigEntry.getKey();
					
					HashMap<String, ThreadConfiguration> cThreadConfig = 
							cThreadConfigEntry.getValue();
					
					WorkerThreadMO cWorkerThreadMO = 
	        				new WorkerThreadMO(
	        						cThreadMO.getcThread(), this.getcWorker());
					
					FacebookWorker cWorker = new FacebookWorker(
							cWorkerThreadMO, 
	        				cThreadConfig, 
	        				this.getcSystemConfig());
					
					this.cThreadPoolExecutor.execute(cWorker);
				}
	        	
	        	this.cThreadPoolExecutor.shutdown();
	        	
	        	this.cThreadPoolExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
	        }
	        
	        return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return cRes;
		}
	}
	*/
	
	//@Timeout
	public void monitorWorkersThreads(/* Timer timer */) 
	{
		String cMethodName = "";
		
		@SuppressWarnings("unused")
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
			/* this.printTimerInfo(timer); */
	        
	        LOG.info(
                    String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s",
                        this.cThreadPoolExecutor.getPoolSize(),
                        this.cThreadPoolExecutor.getCorePoolSize(),
                        this.cThreadPoolExecutor.getActiveCount(),
                        this.cThreadPoolExecutor.getCompletedTaskCount(),
                        this.cThreadPoolExecutor.getTaskCount(),
                        this.cThreadPoolExecutor.isShutdown(),
                        this.cThreadPoolExecutor.isTerminated()));
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
		}
		finally
		{
			try
			{
				this.setSourceItemStatus(
			        		cMethodName,
			        		ToolkitConstants.AMP_FACEBOOK_SOURCE, 
			        		ToolkitConstants.AMP_FACEBOOK_SOURCE,
			        		ToolkitConstants.AMP_FACEBOOK_SOURCE_WORKER,
			        		ToolkitConstants.AMP_DEFAULT_THREAD,
			        		cMethodName, 
						    (cRes = true) ? "Warning" : "Error", 
						    ToolkitConstants.OP_END_CYCLE);
			}
			catch( Exception e)
			{
				LOG.error(cMethodName + "::" + e.getMessage(), e);
			}
		}
	}
	
	//---
	@PreDestroy
	public void releaseResources()
	{
		String cMethodName = "";
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        if ( this.cThreadPoolExecutor != null )
	        {
	        	this.cThreadPoolExecutor.shutdown();
	        }
	        
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage());
			
			e.printStackTrace();
		}
	}
}
