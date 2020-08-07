package com.amp.source.fb.appl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;

import com.amp.source.fb.service.FacebookManagerInitializer;
import com.amp.source.fb.thread.ManagedThreadFactoryImpl;

public class AppServletContextInitializer implements ServletContextListener 
{
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(AppServletContextInitializer.class);

    private static final String SERVLET_JOB_POOL = "SERVLET_JOB_POOL";

    FacebookManagerInitializer initializer;

    public AppServletContextInitializer(FacebookManagerInitializer initializer) {
        this.initializer = initializer;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        LOG.info("contextInitialized. context: " + sce.getServletContext().getServletContextName());
        
        ExecutorService executor = Executors.newSingleThreadExecutor(new ManagedThreadFactoryImpl((SERVLET_JOB_POOL)));
        
        sce.getServletContext().setAttribute(SERVLET_JOB_POOL, executor);
        
        executor.execute(initializer);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) 
    {
        ExecutorService executorService = (ExecutorService) sce.getServletContext().getAttribute(SERVLET_JOB_POOL);
        
        if (executorService != null)
        {
            executorService.shutdown();
            try 
            {
                executorService.awaitTermination(1, TimeUnit.MINUTES);
            } 
            catch (InterruptedException e) 
            {
                LOG.info("Couldn't terminate executorService ", e);
            }
        }
        LOG.info("contextDestroyed. context=" + sce.getServletContext().getServletContextName());
    }
}