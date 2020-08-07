package com.amp.fb.worker;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.amp.source.fb.service.FacebookManagerBean;

public class TestFacebookWorkerBean {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Ignore
	@Test
	public void initBean() 
	{
		@SuppressWarnings("unused")
		boolean cRes = true;
		
		String cMethodName = "";
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        FacebookManagerBean cFBWorkerBean = new FacebookManagerBean();
	        
	        cFBWorkerBean.init();
		}
		catch( Exception e)
		{
			System.out.println(cMethodName + "::" + e.getMessage());
			
			e.printStackTrace();
			
			fail(cMethodName + "::" + e.getMessage()); 
		}
	}
}
