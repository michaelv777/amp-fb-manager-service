package com.amp.source.fb.appl;


import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.amp.source.fb.config.ApplicationConstants;
import com.amp.source.fb.service.FacebookManagerInitializer;

@Configuration
@EnableWebMvc
@EnableTransactionManagement
@EnableAutoConfiguration(exclude = { FreeMarkerAutoConfiguration.class })
@ComponentScan(basePackages = {"com.amp.source.fb", "com.amp.common.api"})
@SpringBootApplication(scanBasePackages = {"com.amp.source.fb", "com.amp.common.api"})
@PropertySource("classpath:" + ApplicationConstants.PROPERTY_FILE_NAME)
public class ManagerApplication extends SpringBootServletInitializer
{
	private static final Logger LOG = 
			LoggerFactory.getLogger(ManagerApplication.class);
	
	@Configuration
    @Profile({ "default", "production" })
    @PropertySource("classpath:" + ApplicationConstants.PROPERTY_FILE_NAME)
    static class ProductionConfiguration 
    {
        @Bean
        public FacebookManagerInitializer serviceInitializer(ServletContext servletContext)
        {
            return new FacebookManagerInitializer(servletContext.getContextPath());
        }

        @Bean
        public ServletContextInitializer appServletContextInitializer(FacebookManagerInitializer initializer)
        {
        	AppServletContextInitializer listener = new AppServletContextInitializer(initializer);
        	
            return new ServletListenerRegistrationBean<>(listener);
        }
        
    }
	
	
	/**
     * This component is used to configure Spring boot when it is used as war application in standalone tomcat
     */
	
	@Component
    public static class WebAppConfigurator extends SpringBootServletInitializer
    {
        @Override
        protected SpringApplicationBuilder configure(SpringApplicationBuilder application) 
        {
            return configureCommon(application);
        }
    }
	
	
	public static void main(String[] args) 
	{
		configureCommon(new SpringApplicationBuilder()).run(args);
	}

	/**
     * This is common configuration for app with embedded tomcat and for standalone war deployment
     * @param application
     * @return
     */
    private static SpringApplicationBuilder configureCommon(SpringApplicationBuilder application)
    {
        return application.sources(ManagerApplication.class).listeners(new ApplicationListener<ContextRefreshedEvent>() 
        {
            @Override
            public void onApplicationEvent(ContextRefreshedEvent event) 
            {
            	LOG.info("============== Spring Application was initialized, event: {}", event);
                
                ApplicationContext applicationContext = event.getApplicationContext();
                
                applicationContext.getBean(FacebookManagerInitializer.class);
            }
        });
    }
}