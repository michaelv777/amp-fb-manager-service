/**
 * 
 */
package com.amp.source.fb.base;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import javax.enterprise.concurrent.ManagedThreadFactory;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.amp.common.api.impl.ToolkitConstants;
import com.amp.common.api.impl.ToolkitDataProvider;
import com.amp.jpa.entities.ThreadConfiguration;
import com.amp.jpa.entities.Worker;
import com.amp.jpa.entities.WorkerConfiguration;
import com.amp.jpa.entities.WorkerThread;
import com.amp.jpaentities.mo.ThreadMO;
import com.amp.source.fb.interfaces.SourceWorkerBeanLocal;
import com.amp.source.fb.interfaces.SourceWorkerBeanRemote;

/**
 * @author MVEKSLER
 *
 */
public abstract class SourceManagerBase extends SourceManagerStorage 
										implements SourceWorkerBeanRemote, 
												   SourceWorkerBeanLocal
{
	private static final Logger LOG = 
			LoggerFactory.getLogger(SourceManagerBase.class);
	
	//---class variables
	@Autowired
	protected ApplicationContext cApplicationContext = null;
	
	@Autowired
	protected ToolkitDataProvider cToolkitDataProvider = null;
	
	protected ManagedThreadFactory cThreadFactory = null;
	
	protected String contextPath = StringUtils.EMPTY;
	
	protected ThreadPoolExecutor cThreadPoolExecutor = null;

	protected Properties cSpringProps = null;
	
	protected Worker cWorker = 
			new Worker();
	
	protected List<WorkerConfiguration> cWorkerConfiguration = 
			new LinkedList<WorkerConfiguration>();
	
	protected List<ThreadConfiguration> cWorkerThreadsConfiguration = 
			new LinkedList<ThreadConfiguration>();
	
	protected HashMap<ThreadMO, HashMap<String, ThreadConfiguration>> cWorkerThreads = 
			new HashMap<ThreadMO, HashMap<String, ThreadConfiguration>>();
	
	protected HashMap<String, String> cSystemConfig = 
			new HashMap<String, String>();
	
	protected HashMap<String, String> cBeanConfig = 
			new HashMap<String, String>();
	
	protected long wkmThreadPoolTimeOut = Long.MAX_VALUE;
	
	protected long wkmKeepAliveThread = 300000;
	
	protected long wkmKeepAliveTimer = 300000;
	
	//---getters/setters
	public ManagedThreadFactory getcThreadFactory() {
		return cThreadFactory;
	}

	public Worker getcWorker() {
		return cWorker;
	}

	public void setcWorker(Worker cWorker) {
		this.cWorker = cWorker;
	}

	public List<ThreadConfiguration> getcWorkerThreadsConfiguration() {
		return cWorkerThreadsConfiguration;
	}

	public void setcWorkerThreadsConfiguration(List<ThreadConfiguration> cWorkerThreadsConfiguration) {
		this.cWorkerThreadsConfiguration = cWorkerThreadsConfiguration;
	}

	public ThreadPoolExecutor getcThreadPoolExecutor() {
		return cThreadPoolExecutor;
	}

	public void setcThreadPoolExecutor(ThreadPoolExecutor cThreadPoolExecutor) {
		this.cThreadPoolExecutor = cThreadPoolExecutor;
	}

	public long getWkmThreadPoolTimeOut() {
		return wkmThreadPoolTimeOut;
	}

	public void setWkmThreadPoolTimeOut(long wkmThreadPoolTimeOut) {
		this.wkmThreadPoolTimeOut = wkmThreadPoolTimeOut;
	}

	public long getWkmKeepAliveThread() {
		return wkmKeepAliveThread;
	}

	public void setWkmKeepAliveThread(long wkmKeepAliveThread) {
		this.wkmKeepAliveThread = wkmKeepAliveThread;
	}

	public long getWkmKeepAliveTimer() {
		return wkmKeepAliveTimer;
	}

	public void setWkmKeepAliveTimer(long wkmKeepAliveTimer) {
		this.wkmKeepAliveTimer = wkmKeepAliveTimer;
	}

	public void setcThreadFactory(ManagedThreadFactory cThreadFactory) {
		this.cThreadFactory = cThreadFactory;
	}
	
	public HashMap<String, String> getcSystemConfig() {
		return cSystemConfig;
	}

	public HashMap<String, String> getcBeanConfig() {
		return cBeanConfig;
	}

	public void setcBeanConfig(HashMap<String, String> cBeanConfig) {
		this.cBeanConfig = cBeanConfig;
	}

	public void setcSystemConfig(HashMap<String, String> cSystemConfig) {
		this.cSystemConfig = cSystemConfig;
	}

	

	public List<WorkerConfiguration> getcWorkerConfiguration() {
		return cWorkerConfiguration;
	}

	public void setcWorkerConfiguration(List<WorkerConfiguration> cWorkerConfiguration) {
		this.cWorkerConfiguration = cWorkerConfiguration;
	}

	public ToolkitDataProvider getcToolkitDataProvider() {
		return cToolkitDataProvider;
	}

	public void setcToolkitDataProvider(ToolkitDataProvider cToolkitDataProvider) {
		this.cToolkitDataProvider = cToolkitDataProvider;
	}

	public Properties getcSpringProps() {
		return cSpringProps;
	}

	public void setcSpringProps(Properties cSpringProps) {
		this.cSpringProps = cSpringProps;
	}
	
	public ApplicationContext getcApplicationContext() {
		return cApplicationContext;
	}

	public void setcApplicationContext(ApplicationContext cApplicationContext) {
		this.cApplicationContext = cApplicationContext;
	}

	public HashMap<ThreadMO, HashMap<String, ThreadConfiguration>> getcWorkerThreads() {
		return cWorkerThreads;
	}

	public void setcWorkerThreads(HashMap<ThreadMO, HashMap<String, ThreadConfiguration>> cWorkerThreads) {
		this.cWorkerThreads = cWorkerThreads;
	}

	//---
	protected boolean init()
	{
		boolean cRes = true;
		
		String  cMethodName = "";
	
		try
    	{
    		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
    		
	        this.cWorkerThreads = new HashMap<ThreadMO, HashMap<String, ThreadConfiguration>>();
	        
	        this.cSystemConfig = new HashMap<String, String>();
    		
    		//---
    		if ( cRes )
    		{
    			if ( null == this.cToolkitDataProvider )
    			{
	    			this.cToolkitDataProvider = (ToolkitDataProvider)
	    						this.cApplicationContext.getBean("toolkitDataProvider");
	    			
	    			List<Class<? extends Object>> clazzes = this.cToolkitDataProvider.
	    					gettDatabase().getPersistanceClasses();
	    			
	    			this.cToolkitDataProvider.
	    					gettDatabase().getHibernateSession(clazzes);
    			}
    		}	
    		//---
    		if ( cRes )
    		{
    			List<Class<? extends Object>> clazzes = this.cToolkitDataProvider.
    					gettDatabase().getPersistanceClasses();
    			
    			this.cToolkitDataProvider.
    					gettDatabase().getHibernateSession(clazzes);
    		}
    		
    		return cRes;	 
    	}
		catch(  NoSuchBeanDefinitionException nbd )
		{
			LOG.error(cMethodName + "::" + nbd.getMessage());
    		
    		return false;
		}
		catch(  BeansException be )
		{
			LOG.error(cMethodName + "::" + be.getMessage());
    		
			return false;
		}
    	catch( Exception e)
    	{
    		LOG.error(cMethodName + "::" + e.getMessage());
    		
    		return false;
    	}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected boolean getWorker(String cWorkerName) 
	{
		String cMethodName = "";
		
		String sqlQuery = "";
		
		Session hbsSession = null;
		
		Transaction tx = null;
		
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	    
	        if ( null == cWorkerName )
    		{
    			LOG.error(cMethodName + "::(null == cWorkerName)");
    			
    			cRes = false;
    		}
	       
    		//------
    		if ( cRes )
    		{
	    		if ( null == this.cToolkitDataProvider )
	    		{
	    			LOG.error(cMethodName + "::cToolkitDataProvider is NULL for the Method:" + cMethodName);
	    			
	    			cRes = false;
	    		}
    		}
    		//------
    		if ( cRes )
    		{
    			sqlQuery = this.cToolkitDataProvider.gettSQL().getSqlQueryByFunctionName(cMethodName);
    			
    			if ( null == sqlQuery || StringUtils.isEmpty(sqlQuery))
        		{
        			LOG.error(cMethodName + "::sqlQuery is NULL for the Method:" + cMethodName);
        			
        			cRes = false;
        		}
    		}
    		//------
    		if ( cRes )
    		{
    			hbsSession = this.cToolkitDataProvider.gettDatabase().getHbsSessions().openSession();
    			
    			NativeQuery cQuery = hbsSession.createSQLQuery(sqlQuery);
    			
    			cQuery.addEntity(Worker.class);
    			
    			cQuery.setParameter("workerName", cWorkerName);
    			
    			tx = hbsSession.beginTransaction();
    			
    			List<Worker> cWorkers = (List<Worker>)cQuery.list();
    			
    			if ( cWorkers != null && cWorkers.size() >= 1 )
    			{
    				this.cWorker = cWorkers.get(0);
    			}
				
				if ( null == this.cWorker )
				{
					LOG.error(cMethodName + "::cConfiguration  is NULL!");
					
					cRes = false;
				}
    		}
    		
    		if ( tx != null )
			{
				tx.commit();
			}
    		
    		return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return false;
		}
		finally
		{
			
			if ( hbsSession != null )
    		{
    			hbsSession.close();
    		}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected boolean getWorkerConfig(String cWorkerName) 
	{
		String cMethodName = "";
		
		String sqlQuery = "";
		
		Session hbsSession = null;
		
		Transaction tx = null;
		
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	    
	        if ( null == cWorkerName )
    		{
    			LOG.error(cMethodName + "::(null == cWorkerName)");
    			
    			cRes = false;
    		}
	       
    		//------
    		if ( cRes )
    		{
	    		if ( null == this.cToolkitDataProvider )
	    		{
	    			LOG.error(cMethodName + "::cToolkitDataProvider is NULL for the Method:" + cMethodName);
	    			
	    			cRes = false;
	    		}
    		}
    		//------
    		if ( cRes )
    		{
    			sqlQuery = this.cToolkitDataProvider.gettSQL().getSqlQueryByFunctionName(cMethodName);
    			
    			if ( null == sqlQuery || StringUtils.isEmpty(sqlQuery))
        		{
        			LOG.error(cMethodName + "::sqlQuery is NULL for the Method:" + cMethodName);
        			
        			cRes = false;
        		}
    		}
    		//------
    		if ( cRes )
    		{
    			hbsSession = this.cToolkitDataProvider.gettDatabase().getHbsSessions().openSession();
    			
    			NativeQuery cQuery = hbsSession.createSQLQuery(sqlQuery);
    			
    			cQuery.addEntity(WorkerConfiguration.class);
    			
    			cQuery.setParameter("workerName", cWorkerName);
    			
    			tx = hbsSession.beginTransaction();
    			
				this.cWorkerConfiguration = (List<WorkerConfiguration>)cQuery.list();
				
				if ( null == this.cWorkerConfiguration )
				{
					LOG.error(cMethodName + "::cConfiguration  is NULL!");
					
					cRes = false;
				}
    		}
    		
    		if ( tx != null )
			{
				tx.commit();
			}
    		
    		return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return false;
		}
		finally
		{
			
			if ( hbsSession != null )
    		{
    			hbsSession.close();
    		}
		}
	}
	
	protected boolean setWorkerThreadsConfiguration() 
	{
		String cMethodName = "";
		
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	       
    		//------
    		if ( cRes )
    		{
				if ( this.getcWorker() != null )
				{
					if ( this.getcWorker().getWorkerThreads() != null )
					{
    					for ( WorkerThread cWorkerThread : this.getcWorker().getWorkerThreads() )
    					{
    						com.amp.jpa.entities.Thread cThread = cWorkerThread.getThread();
    						
    						if ( cThread != null )
    						{
    							Set<ThreadConfiguration> cThreadConfigurations = cThread.getThreadConfigurations();
    							
    							if ( cThreadConfigurations != null )
    							{
    								this.getcWorkerThreadsConfiguration().addAll(cThreadConfigurations);
    							}
    						}
    					}
					}
				}
    		}
    		
    		return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return false;
		}
	}
	
	
	protected boolean setWorkerConfiguration() 
	{
		String cMethodName = "";
		
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	    
	        if ( null == this.getcWorker() )
    		{
    			LOG.error(cMethodName + "::(null == cWorker)");
    			
    			cRes = false;
    		}
	       
    		
    		if ( cRes )
    		{
				this.getcWorkerConfiguration().addAll(
						this.getcWorker().getWorkerConfigurations());
				
				if ( null == this.getcWorkerConfiguration() )
				{
					LOG.error(cMethodName + "::cConfiguration  is NULL!");
					
					cRes = false;
				}
    		}
    		return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return false;
		}
	}
	
	/**
	 * 
	 */
	protected boolean setWorkerThreads() 
	{
		String cMethodName = "";
		
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        for( ThreadConfiguration cThreadConfiguration : this.cWorkerThreadsConfiguration)
	        {
        		String cConfigKey   = cThreadConfiguration.getConfigkey();
				String cConfigValue = cThreadConfiguration.getConfigvalue();
        		
				com.amp.jpa.entities.Thread cThread = cThreadConfiguration.getThread();
				
				ThreadMO cThreadMO = 
						new ThreadMO(cThread);
				
        		LOG.info(cMethodName +
        				      ",Source="       + cThreadConfiguration.getSource().getName() +
        				      ",Thread="       + cThreadConfiguration.getThread().getName() +
	      					  ",cConfigKey="   + cConfigKey   + 
	      					  ",cConfigValue=" + cConfigValue);
        		
        		if ( this.cWorkerThreads.containsKey(cThreadMO))
        		{
        			HashMap<String, ThreadConfiguration> cThreadConfig = 
        					this.cWorkerThreads.get(cThreadMO);
        			
        			cThreadConfig.put(cConfigKey, cThreadConfiguration);
        		}
        		else
        		{
        			HashMap<String, ThreadConfiguration> cThreadConfig = 
        					new HashMap<String, ThreadConfiguration>();
        			
        			cThreadConfig.put(cConfigKey, cThreadConfiguration);
        			
        			this.cWorkerThreads.put(cThreadMO, cThreadConfig);
        		}
	        }
	        
	        return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return false;
		}
	}

	/**
	 * 
	 */
	protected boolean setSystemProperties() 
	{
		String cMethodName = "";
		
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        for( WorkerConfiguration cConfiguration : this.cWorkerConfiguration )
	        {
	        	if ( (cConfiguration.getSource().getName().
	        			equals(ToolkitConstants.AMP_RUNTIME_SOURCE)) &&
	        		  cConfiguration.getSource().getSourcetypeM().getName().
	        		  	equals(ToolkitConstants.AMP_RUNTIME_SOURCE))
	        	{
	        		String cConfigKey   = cConfiguration.getConfigkey();
	        		String cConfigValue = cConfiguration.getConfigvalue();
	        		
	        		this.cSystemConfig.put(cConfigKey, cConfigValue);
	        		
	        		if ( System.getProperty(cConfigKey) != null )
	        		{
	        			System.clearProperty(cConfigKey);
	        		}
	        		
	        		System.setProperty(cConfigKey, cConfigValue);
	        	}
	        }
	       
	        return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return false;
		}
	}
	
	/**
	 * 
	 */
	protected boolean setManagerProperties() 
	{
		String cMethodName = "";
		
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        HashMap<String, Field> cFields = this.getInheritedFields(this.getClass()) ;
	        
	        for( WorkerConfiguration cConfiguration : this.cWorkerConfiguration )
	        {
	        	String cConfigKey   = cConfiguration.getConfigkey();
        		String cConfigValue = cConfiguration.getConfigvalue();
        		
	        	if ( (cConfiguration.getSource().getName().
	        			equals(ToolkitConstants.AMP_FACEBOOK_SOURCE)) )
	        	{
	        		this.cBeanConfig.put(cConfigKey, cConfigValue);
	        	
	        		if ( cFields.containsKey(cConfigKey) )
	        		{
	        			Field cField = cFields.get(cConfigKey);
						
	        			Type type = (Type) cField.getGenericType();
					  	
	        			if ( type.equals(String.class ))
	        			{
	        				cField.set(this, cConfigValue);
	        			}
	        			else if ( type.equals(boolean.class ))
	        			{
	        				boolean cBoolSet = Boolean.parseBoolean(cConfigValue);
	        				cField.setBoolean(this, cBoolSet);	
	        			}
	        			else if ( type.equals(int.class ))
	        			{
	        				int cIntSet = Integer.parseInt(cConfigValue);
	        				cField.setInt(this, cIntSet);	
	        			}
	        			else if ( type.equals(long.class ))
	        			{
	        				long cIntSet = Long.parseLong(cConfigValue);
	        				cField.setLong(this, cIntSet);	
	        			}
	        		}
	        		
		        	
	        	}
	        }
	        
	        /*
	        if ( this.cBeanConfig.containsKey(ToolkitConstants.AMP_KEEP_ALIVE_THREAD))
			{
				try
				{
					String cKeepAliveTimeStr = this.cBeanConfig.get(ToolkitConstants.AMP_KEEP_ALIVE_THREAD);
					
					this.cKeepAliveThread = Long.valueOf(cKeepAliveTimeStr);
				}
				catch(Exception e)
				{
					cLogger.error(cMethodName + "::Check Settings for:" + ToolkitConstants.AMP_KEEP_ALIVE_THREAD);
				}
			}
	        
	        if ( this.cBeanConfig.containsKey(ToolkitConstants.AMP_KEEP_ALIVE_TIMER))
			{
				try
				{
					String cKeepAliveTimerStr = this.cBeanConfig.get(ToolkitConstants.AMP_KEEP_ALIVE_TIMER);
					
					this.cKeepAliveTimer = Long.valueOf(cKeepAliveTimerStr);
				}
				catch(Exception e)
				{
					cLogger.error(cMethodName + "::Check Settings for:" + ToolkitConstants.AMP_KEEP_ALIVE_THREAD);
				}
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
	
	/**
	 * @param timer
	 * @param cMethodName
	 */
	/*
	protected boolean printTimerInfo(Timer timer) 
	{
		boolean cRes = true;
		
		String cMethodName = "";
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        if ( timer == null )
	        {
	        	LOG.info(cMethodName + "::timer is null");
	        	
	        	return false;
	        }
	        
	        LOG.info(cMethodName + "::" + "Timer Service : "  + timer.getInfo());
	        LOG.info(cMethodName + "::" + "Current Time : "   + new Date());
	        LOG.info(cMethodName + "::" + "Next Timeout : "   + timer.getNextTimeout());
	        LOG.info(cMethodName + "::" + "Time Remaining : " + timer.getTimeRemaining());
	        LOG.info("____________________________________________");
			
			return cRes;
        
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return false;
		}
	}
	*/
}
