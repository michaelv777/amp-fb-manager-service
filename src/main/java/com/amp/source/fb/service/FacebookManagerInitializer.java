package com.amp.source.fb.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class FacebookManagerInitializer implements Runnable 
{
    
	private static Logger LOG = 
			LoggerFactory.getLogger(FacebookManagerInitializer.class);
    
    @Autowired
    private ApplicationContext applicationContext;
    
    protected String contextPath;
    
    public FacebookManagerInitializer(String contextPath) 
    {
        this.contextPath = contextPath;
    }
    
    @Override
    public void run() 
    {
       
        LOG.info("[FBMANAGER-INIT] About to start service initialization process");
        
        try  
        {
        	FacebookManagerBean managerBean = (FacebookManagerBean)
        			this.applicationContext.getBean("FacebookManagerBean");
        	
        	managerBean.start();
        } 
        catch( Exception e )
        {
        	LOG.error(e.getMessage(), e);
        }
    }
}
