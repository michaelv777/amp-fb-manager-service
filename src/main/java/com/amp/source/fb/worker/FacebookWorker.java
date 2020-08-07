package com.amp.source.fb.worker;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amp.amazon.webservices.rest.Item;
import com.amp.amazon.webservices.rest.Items;
import com.amp.common.api.impl.ToolkitConstants;
import com.amp.data.handler.aws.DataHandlerAWS;
import com.amp.data.handler.base.DataHandlerI;
import com.amp.jpa.entities.Source;
import com.amp.jpa.entities.ThreadConfiguration;
import com.amp.jpa.entities.WorkerData;
import com.amp.jpaentities.mo.CategoryMO;
import com.amp.jpaentities.mo.WorkerDataListMO;
import com.amp.jpaentities.mo.WorkerDataMO;
import com.amp.jpaentities.mo.WorkerThreadMO;
import com.amp.source.fb.base.ThreadWorker;
import com.amp.text.processor.api.impl.TextProcessorDandelionImpl;
import com.amp.text.processor.api.impl.TextProcessorWatsonImpl;
import com.amp.text.processor.api.interfaces.TextProcessorInterface;
import com.amp.text.translator.api.impl.TextTranslatorGoogleImpl;
import com.amp.text.translator.api.interfaces.TextTranslatorInterface;
import com.fb.restfb.api.impl.RestFacebookWorker;
import com.fb.restfb.api.interfaces.RestFacebookInterface;
import com.fb.restfb.api.mo.PostMO;
import com.restfb.types.CategorizedFacebookType;
import com.restfb.types.Post;

/**
 * Session Bean implementation class FacebookWorkerBean
 */
public class FacebookWorker extends ThreadWorker implements Runnable
{
	private static final Logger LOG = 
			LoggerFactory.getLogger(FacebookWorker.class);
	
	//---class variables
	protected RestFacebookInterface cRestFacebookWorker = 
			new RestFacebookWorker();
	
	protected List<PostMO> cPosts = 
			new LinkedList<PostMO>();
	
	
	protected boolean wkIsProcessComments     = false;
	protected boolean wkIsSendPostsAdvLinks   = false;
	protected boolean wkIsResendPostsAdvLinks = false;
	
	protected long wkInitialTimeout   = 300000;
	protected long wkIntervalDuration = 300000;
	
	protected long wkGetPostsMinutesAgo       = 43200;
	protected long wkResendPostsAdvLinksCycle = 21600;
	
	protected int  wkSendPostAdvLinksNum = 1;
	protected int  wkPageLimit = 10;
	
	protected String wkGroupId = "";
	
	//---getters/setters
	public List<PostMO> getcPosts() {
		return cPosts;
	}

	public boolean isWkIsProcessComments() {
		return wkIsProcessComments;
	}

	public void setWkIsProcessComments(boolean wkIsProcessComments) {
		this.wkIsProcessComments = wkIsProcessComments;
	}

	public boolean isWkIsSendPostsAdvLinks() {
		return wkIsSendPostsAdvLinks;
	}

	public void setWkIsSendPostsAdvLinks(boolean wkIsSendPostsAdvLinks) {
		this.wkIsSendPostsAdvLinks = wkIsSendPostsAdvLinks;
	}

	public boolean isWkIsResendPostsAdvLinks() {
		return wkIsResendPostsAdvLinks;
	}

	public void setWkIsResendPostsAdvLinks(boolean wkIsResendPostsAdvLinks) {
		this.wkIsResendPostsAdvLinks = wkIsResendPostsAdvLinks;
	}

	public long getWkInitialTimeout() {
		return wkInitialTimeout;
	}

	public void setWkInitialTimeout(long wkInitialTimeout) {
		this.wkInitialTimeout = wkInitialTimeout;
	}

	public long getWkIntervalDuration() {
		return wkIntervalDuration;
	}

	public void setWkIntervalDuration(long wkIntervalDuration) {
		this.wkIntervalDuration = wkIntervalDuration;
	}

	public long getWkGetPostsMinutesAgo() {
		return wkGetPostsMinutesAgo;
	}

	public void setWkGetPostsMinutesAgo(long wkGetPostsMinutesAgo) {
		this.wkGetPostsMinutesAgo = wkGetPostsMinutesAgo;
	}

	public long getWkResendPostsAdvLinksCycle() {
		return wkResendPostsAdvLinksCycle;
	}

	public void setWkResendPostsAdvLinksCycle(long wkResendPostsAdvLinksCycle) {
		this.wkResendPostsAdvLinksCycle = wkResendPostsAdvLinksCycle;
	}

	public int getWkSendPostAdvLinksNum() {
		return wkSendPostAdvLinksNum;
	}

	public void setWkSendPostAdvLinksNum(int wkSendPostAdvLinksNum) {
		this.wkSendPostAdvLinksNum = wkSendPostAdvLinksNum;
	}

	public int getWkPageLimit() {
		return wkPageLimit;
	}

	public void setWkPageLimit(int wkPageLimit) {
		this.wkPageLimit = wkPageLimit;
	}

	public String getWkGroupId() {
		return wkGroupId;
	}

	public void setWkGroupId(String wkGroupId) {
		this.wkGroupId = wkGroupId;
	}

	public void setcPosts(List<PostMO> cPosts) {
		this.cPosts = cPosts;
	}

	public RestFacebookInterface getcRestFacebookWorker() {
		return cRestFacebookWorker;
	}

	public void setcRestFacebookWorker(RestFacebookInterface cRestFacebookWorker) {
		this.cRestFacebookWorker = cRestFacebookWorker;
	}

	//---class methods
    public FacebookWorker() 
    {
		String cMethodName = "";

		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        super.init();
	        
	        this.init();
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
		}
    }

    public FacebookWorker(WorkerThreadMO cWorkerThreadMO, 
    					  HashMap<String, ThreadConfiguration> cThreadConfig) 
    {
    	String cMethodName = "";

		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        super.init();
	        
	        this.setcWorkerThreadMO(cWorkerThreadMO);
	        
	        this.setcThreadConfiguration(cThreadConfig);
	        
	        this.setThreadConfigurationMap(cThreadConfig);
	        
	        this.init();
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
		}
	}
    
    public FacebookWorker(WorkerThreadMO cWorkerThreadMO, 
			  			  HashMap<String, ThreadConfiguration> cThreadConfig,
    				      HashMap<String, String> cSystemConfig) 
    {
    	String cMethodName = "";

		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        super.init();
	        
	        this.setcWorkerThreadMO(cWorkerThreadMO);
	        
	        this.setcThreadConfiguration(cThreadConfig);
	        
	        this.setcSystemConfiguration(cSystemConfig);
	        
	        this.setThreadConfigurationMap(cThreadConfig);
	        
	        this.init();
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
		}
	}

	@Override
	public void run() 
	{
		String cMethodName = "";
		
		@SuppressWarnings("unused")
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        /*if ( this.wkInitialTimeout > 0 )
        	{
        		Thread.sleep(this.wkInitialTimeout);
        	}*/
	        
	        while ( this.wkIsRunThread )
	        {
	        	this.initNextCycle();
	        	
	        	this.processWorkerData();
		        
	        	this.saveWorkerData();
		        
		        Thread.sleep(this.wkIntervalDuration);
	        }
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
		}
	}

	@Override
	protected boolean init()
	{
		String cMethodName = "";
		
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        
	        cMethodName = ste.getMethodName();
	     
	        this.setSystemConfiguration();
	     
	        this.setWorkerThreadConfiguration();
	        
	        this.cRestFacebookWorker = new RestFacebookWorker(
	        		this.cSystemConfiguration, 
	        		this.cThreadConfigurationMap);
		
	        return cRes;
		}
		catch( NumberFormatException e )
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return cRes;
		}
	}

	/**
	 * 
	 */
	protected boolean initNextCycle() 
	{
		String cMethodName = "";
	
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        this.cPosts.clear();
			
			return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return cRes;
		}
	}

	protected boolean setSystemConfiguration() 
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
	
	/**
	 * @throws IllegalAccessException
	 */
	protected boolean setWorkerThreadConfiguration()
	{
		String cMethodName = "";
		
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
			Field[] cFields = this.getClass().getDeclaredFields();
			
			for( int jondex = 0; jondex < cFields.length; ++jondex )
			{
				Field cField = (Field)cFields[jondex];
				
				String cSourceConfigKey = cField.getName();
						 
				if ( this.cThreadConfiguration.containsKey(cSourceConfigKey))
				{
					String cSourceConfigValue = this.cThreadConfiguration.
							get(cSourceConfigKey).getConfigvalue();
					
					  Type type = (Type) cField.getGenericType();
					  	
					  if ( type.equals(String.class ))
					  {
						  cField.set(this, cSourceConfigValue);
					  }
					  else if ( type.equals(boolean.class ))
					  {
						  boolean cBoolSet = Boolean.parseBoolean(cSourceConfigValue);
						  cField.setBoolean(this, cBoolSet);	
					  }
					  else if ( type.equals(int.class ))
					  {
						  int cIntSet = Integer.parseInt(cSourceConfigValue);
						  cField.setInt(this, cIntSet);	
					  }
					  else if ( type.equals(long.class ))
					  {
						  long cIntSet = Long.parseLong(cSourceConfigValue);
						  cField.setLong(this, cIntSet);	
					  }
				}
			}
			//---set super class configuration
			cFields = getClass().getSuperclass().getDeclaredFields();
			
			for( int jondex = 0; jondex < cFields.length; ++jondex )
			{
				Field cField = (Field)cFields[jondex];
				
				String cSourceConfigKey = cField.getName();
						 
				if ( this.cThreadConfiguration.containsKey(cSourceConfigKey))
				{
					String cSourceConfigValue = this.cThreadConfiguration.
							get(cSourceConfigKey).getConfigvalue();
					
					  Type type = (Type) cField.getGenericType();
					  	
					  if ( type.equals(String.class ))
					  {
						  cField.set(this, cSourceConfigValue);
					  }
					  else if ( type.equals(boolean.class ))
					  {
						  boolean cBoolSet = Boolean.parseBoolean(cSourceConfigValue);
						  cField.setBoolean(this, cBoolSet);	
					  }
					  else if ( type.equals(int.class ))
					  {
						  int cIntSet = Integer.parseInt(cSourceConfigValue);
						  cField.setInt(this, cIntSet);	
					  }
					  else if ( type.equals(long.class ))
					  {
						  long cIntSet = Long.parseLong(cSourceConfigValue);
						  cField.setLong(this, cIntSet);	
					  }
				}
			}
			
			return cRes;
		}
		catch( IllegalAccessException e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return cRes;
		}
	}

	/**
	 * 
	 */
	protected boolean processWorkerData() 
	{
		String cMethodName = "";
		
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
			List<Post> cPosts = this.cRestFacebookWorker.getGroupFeeds(
					this.wkGroupId, 
					this.wkPageLimit, 
					this.wkGetPostsMinutesAgo);
			
			for( Post cPost : cPosts )
			{
				boolean cPostRes = true; 
				
				PostMO cPostMO = new PostMO(cPost, this.wkGroupId);
				
				this.cPosts.add(cPostMO);
				
			    if ( this.isProcessFacebookPost(cPostMO) )
			    {
			    	cPostRes = this.processFacebookPost(cPostMO);
			    }
				
			    String cPostStatus = (cPostRes == true ? 
		    			ToolkitConstants.AMP_STATUS_NORMAL : 
		    			ToolkitConstants.AMP_STATUS_WARNING);
			    
				this.setItemOpStatus(cPostMO.getcPost().getId(), 
									 cPostMO.getcPost().getId(), 
									 cPostStatus, 
									 ToolkitConstants.OP_PROCESS_POST);
			}
			
			return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);			
			
			return cRes;
		}
	}

	/**
	 * @param cMethodName
	 * @param cPost
	 */
	protected boolean processFacebookPost(PostMO cPost) 
	{
		boolean cRes = true;
		
		String cMethodName = "";

		String cTranslatedText = "";
	
		String cPostMessage = "";
		
		LinkedHashMap<Double, String> cTextKeywords = 
				new LinkedHashMap<Double, String>();
		
		List<Items> cSearchItems = 
				new LinkedList<Items>();
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        if ( null == cPost )
	        {
	        	cRes = false;
	        }
	        //---get post message
	        if ( cRes )
	        {
	        	cPostMessage = cPost.getPostMessge();
	        	if ( StringUtils.isBlank(cPostMessage))
	        	{
	        		cRes = false;
	        	}
	        }
	        //---translate post message
	        if ( cRes )
	        {
				cTranslatedText = this.translateText(cPostMessage);
				if ( StringUtils.isBlank(cTranslatedText))
	        	{
	        		cRes = false;
	        	}
	        }	
	        //--get message keywords
	        if ( cRes )
	        {
	        	cTextKeywords = this.analyzeTextKeywordsWithWatson(cTranslatedText);
	        }
	        //---get amazon related products
	        if ( cRes )
	        {
	        	cSearchItems.addAll(this.searchAmazonForItems(cTextKeywords));
	        }
	        //---send post adv. links
	        if ( cRes )
	        {
	        	cRes = this.processAmazonItems(cPost, cSearchItems);
	        }
	        
	        return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return cRes;
		}
	}

	/**
	 * @param cPost
	 * @param cSearchItems
	 */
	protected boolean processAmazonItems(PostMO cPost, List<Items> cSearchItems) 
	{
		String cMethodName = "";
		
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        this.sendPostAdvLinks(cPost, cSearchItems);
			
			return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);			
			
			return cRes;
		}
	}

	/**
	 * @param cPost
	 * @param cSearchItems
	 * @return
	 */
	protected boolean sendPostAdvLinks(PostMO cPost, List<Items> cSearchItems) 
	{
		String cItemURL  = "";
		String cMethodName = "";
		
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
			String cItemId = cPost.getcPost().getId();
			
			int sendPostLinkNum = 0;
			
			for( Items cItemsM : cSearchItems )
			{
				List<Item> cItems = cItemsM.getItem();
				
				for( Item cItem : cItems )
				{
					if ( sendPostLinkNum < this.wkSendPostAdvLinksNum )
					{
						boolean cSendLinkRes = true;
						
						cItemURL  = cItem.getDetailPageURL();
						
						if ( cSendLinkRes )
						{
							cSendLinkRes = 
									this.cRestFacebookWorker.publishPostComment(
									cItemId, 
									cItemURL, 
									cItemURL);
						}
						
						if ( cSendLinkRes )
						{
							this.setItemOpStatus(
						        	cItemId, 
						        	cItemURL, 
						        	ToolkitConstants.AMP_STATUS_NOT_INPROCESS, 
									ToolkitConstants.OP_POST_LINK);
						}
						else
						{
							this.setItemOpStatus(
				        			cItemId, 
				        			cItemURL, 
				        			ToolkitConstants.AMP_STATUS_CRITICAL, 
									ToolkitConstants.OP_POST_LINK);
						}
						
						++sendPostLinkNum;
						
						LOG.info("M.V. Custom::Send Link" + cMethodName + "::" + cItem.getDetailPageURL());
						
						LOG.debug("M.V. Custom::Send Link" + cMethodName + "::" + cItem.getDetailPageURL());
					}
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

	/**
	 * @param cItemId
	 * @param isSendPostAdvLinks
	 * @return
	 */
	protected boolean isProcessFacebookPost(PostMO cPost)
	{
		String cMethodName = "";
		
		boolean isProcessPost = false;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
		
			boolean isSendPostAdLinks = this.isSendPostAdLinks(cPost);
			
			boolean isSendPostAdStatus = this.isSendPostAdStatus(cPost);
			
			boolean isPostUserPermitted = this.isPostUserPermitted(cPost);
			
			isProcessPost = (isSendPostAdLinks && 
							 isSendPostAdStatus && 
							 isPostUserPermitted);
			
			return isProcessPost;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return isProcessPost;
		}
	}

	/**
	 * @return
	 */
	protected boolean isSendPostAdLinks(PostMO cPost) 
	{
		String cMethodName = "";
		
		boolean isSendPostAdvLinks = false;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
			
	        if ( !this.wkIsSendPostsAdvLinks ) 
	        {
	        	return false;
	        }
	        
			String cItemId = cPost.getcPost().getId();
			
			WorkerDataListMO cWorkerDataListMO = this.getItemOpStatus(
			        		this.getWkGroupId(),
			        		this.getWkGroupId(),
			        		//ToolkitConstants.AMP_FACEBOOK_SOURCE_WORKER,
			        		this.getcWorkerThreadMO().getcWorker().getName(),
			        		this.getcWorkerThreadMO().getcThread().getName(),
			    			cItemId, 
							ToolkitConstants.OP_POST_LINK);
		     
			if ( cWorkerDataListMO != null )
			{
				if ( cWorkerDataListMO.cWorkerData.size() == 0 )
				{
					isSendPostAdvLinks = true;
				}
				else if ( cWorkerDataListMO.cWorkerData.size() > 0 )
				{
					for ( WorkerDataMO cWorkerDataMO : cWorkerDataListMO.cWorkerData)
					{
						WorkerData cWorkerData = cWorkerDataMO.cWorkerData;
						
						//---if the same item and no itemId != ERROR
						if ( cWorkerData.getItemid().equals(cItemId) )
						{
							Date cCurrDate = Calendar.getInstance().getTime();
							Date cLinkDate = cWorkerData.getUpdatedate();
							
							long cTimeDiff = cCurrDate.getTime() - cLinkDate.getTime();
							
							long cMinDiff = TimeUnit.MILLISECONDS.toMinutes(cTimeDiff);
							
							if ( (cMinDiff >= this.wkResendPostsAdvLinksCycle) && (this.wkIsResendPostsAdvLinks) )
							{
								isSendPostAdvLinks = true;
							}
						}
					}
				}
			}
			
			return isSendPostAdvLinks;
		
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return isSendPostAdvLinks;
		}
	}

	protected boolean isSendPostAdStatus(PostMO cPost) 
	{
		String cMethodName = "";
		
		boolean isSendPostAdvLinks = false;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
			
			String cItemId = cPost.getcPost().getId();
			
			WorkerDataListMO cWorkerDataListMO = this.getItemOpStatus(
			        		this.getWkGroupId(),
			        		this.getWkGroupId(),
			        		//ToolkitConstants.AMP_FACEBOOK_SOURCE_WORKER,
			        		this.getcWorkerThreadMO().getcWorker().getName(),
			        		this.getcWorkerThreadMO().getcThread().getName(),
			    			cItemId, 
							ToolkitConstants.OP_POST_LINK);
		     
			
			if ( cWorkerDataListMO != null )
			{
				if ( cWorkerDataListMO.cWorkerData.size() == 0 )
				{
					isSendPostAdvLinks = true;
				}
				else if ( cWorkerDataListMO.cWorkerData.size() > 0 )
				{
					for ( WorkerDataMO cWorkerDataMO : cWorkerDataListMO.cWorkerData)
					{
						WorkerData cWorkerData = cWorkerDataMO.cWorkerData;
						
						//---if the same item and no itemId != ERROR
						if ( cWorkerData.getItemid().equals(cItemId) )
						{
							String cPostStatus = cWorkerData.getStatusM().getName();
							
							if ( !cPostStatus.equals(ToolkitConstants.AMP_STATUS_NOT_INPROCESS) )
							{
								isSendPostAdvLinks = true;
							}
						}
					}
				}
			}
			
			return isSendPostAdvLinks;
		
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return isSendPostAdvLinks;
		}
	}
	
	/**
	 * @return
	 */
	protected boolean isPostUserPermitted(PostMO cPost) 
	{
		String cMethodName = "";
		
		boolean isPostUserPermitted = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
			
			CategorizedFacebookType cCategorizedFacebookType = cPost.getcPost().getFrom();
			
			String cUserId = cCategorizedFacebookType.getId();
			
			String cUserName = cCategorizedFacebookType.getName();
			
			LOG.info(cMethodName + "::" + 
										  cUserId + ":" + cUserName);
			
			/*if ( cUserName.equals("Michael Veksler") )
			{
				isPostUserPermitted = false;
			}*/
			
			return isPostUserPermitted;
		
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return isPostUserPermitted;
		}
	}
	
	/**
	 * @param cSearchItemsM
	 */
	protected boolean printAmazonItems(List<Items> cSearchItemsM) 
	{
		String cMethodName = "";
	
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
			for( Items cItemsM : cSearchItemsM )
			{
				List<Item> cItems = cItemsM.getItem();
				
				for( Item cItem : cItems )
				{
					LOG.info(cMethodName + "::" + cItem.getDetailPageURL());
					
					LOG.debug(cMethodName + "::" + cItem.getDetailPageURL());
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
    //---
	
    protected boolean setThreadConfigurationMap(HashMap<String, ThreadConfiguration> cThreadConfig) 
	{
		String cMethodName = "";
	
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        for( Map.Entry<String, ThreadConfiguration> cThreadConfigEntry : cThreadConfig.entrySet())
	        {
	        	ThreadConfiguration cThreadConfigValue = cThreadConfigEntry.getValue();
	        	
	        	String cConfigKey   = cThreadConfigValue.getConfigkey();
	        	String cConfigValue = cThreadConfigValue.getConfigvalue();
	        	
	        	this.cThreadConfigurationMap.put(cConfigKey, cConfigValue);
	        }
	        
	        return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return cRes;
		}
	}

	/**
	 * 
	 */
	@Override
	protected boolean saveWorkerData() 
	{
		String cMethodName = "";
	
		boolean cRes = true;
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        
	        //--set hroup cycle status
	      
	        String cItemId = this.wkGroupId;
	        
	        cRes = this.setItemOpStatus(cItemId, 
	        						    cItemId, 
	        						    ToolkitConstants.AMP_STATUS_NORMAL, 
	        						    ToolkitConstants.OP_END_CYCLE);
			
			return cRes;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);			
			
			return cRes;
		}
	}

	public String translateText(String text) 
	{
		String cMethodName = "";
	
		String cTranslatedText = "";
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        	   
	        String textEncoded = URLEncoder.encode(text, "UTF-8");
        
	        TextTranslatorInterface cTextProcessor = 
	        		new TextTranslatorGoogleImpl(this.cSystemConfiguration);
	        //TextTranslatorInterface cTextProcessor = new TextTranslatorGoogleImpl();
	        		
	        String jsonResponse = cTextProcessor.translateTextByGet(textEncoded);
	        
	        LOG.info(cMethodName + "::" + jsonResponse);
	        
	        if ( !StringUtils.isEmpty(jsonResponse))
	        {
	        	cTranslatedText = cTextProcessor.getTranslatedData(jsonResponse);
	        	
	        	LOG.info(cMethodName + "::cKeywrodsText=" + cTranslatedText);
	        }
	        
	        return cTranslatedText;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return text;
		}
	}
    
    public LinkedHashMap<Double, String> analyzeTextKeywordsWithWatson(String text) 
	{
		String cMethodName = "";
	
		LinkedHashMap<Double, String> cTextKeywords = 
				new LinkedHashMap<Double, String>();
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        		
	        HashMap<String, String> params = new HashMap<String, String>();
	        params.put("text", text);
	        params.put("isEntitiesEmotion", "false");
	        params.put("isEntitiesSentiment", "false");
	        params.put("cEntitiesLimit", "1");
	        params.put("isKeywordsEmotion", "true");
	        params.put("isKeywordsSentiment", "true");
	        params.put("cKeywordsLimit", "2");
        
	        TextProcessorInterface cTextProcessor = 
	        		new TextProcessorWatsonImpl(this.cSystemConfiguration, 
	        								    this.cThreadConfigurationMap);
        	
	        String jsonResponse = cTextProcessor.extractDataFromTextByPost(params);
	        
	        LOG.info(cMethodName + "::" + jsonResponse);
	        
	        if ( !StringUtils.isEmpty(jsonResponse))
	        {
	        	cTextKeywords = cTextProcessor.getKeywordsFromJSONData(jsonResponse, 1, true);
	        	
	        	LOG.info(cMethodName + "::cKeywrodsText=" + cTextKeywords);
	        }
	        
	        return cTextKeywords;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return new LinkedHashMap<Double, String>();
		}
	}
    
    public String analyzeTextKeywordsWithDandelion(String text) 
	{
		String cMethodName = "";
	
		String cTextKeywords = "";
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	        		
	        HashMap<String, String> params = new HashMap<String, String>();
	        params.put("text", text);
	        params.put("min_confidence", "0.5");
	        params.put("social.hashtag", "true");
	        params.put("social.mention", "true");
	        params.put("include", "types, categories");
        
	        TextProcessorInterface cTextProcessor = 
	        		new TextProcessorDandelionImpl(this.cSystemConfiguration);
        	
	        String jsonResponse = cTextProcessor.extractDataFromTextByGet(params);
	        
	        LOG.info(cMethodName + "::" + jsonResponse);
	        
	        if ( !StringUtils.isEmpty(jsonResponse))
	        {
	        	cTextKeywords = cTextProcessor.getKeywordsFromJSONData(jsonResponse);
	        	
	        	LOG.info(cMethodName + "::cKeywrodsText=" + cTextKeywords);
	        }
	        
	        return cTextKeywords;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return "";
		}
	}
    
    public List<Items> searchAmazonForItems(LinkedHashMap<Double, String> cTextKeywords)
 	{
 		String cMethodName = "";

 		List<Items> cSearchItems = new LinkedList<Items>();
 		
 		@SuppressWarnings("unused")
		boolean cRes = true;

 		try 
 		{
 			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
 			StackTraceElement ste = stacktrace[1];
 			cMethodName = ste.getMethodName();
 			
 			if ( cTextKeywords == null )
 			{
 				LOG.info(cMethodName + "::cTextKeywords is null!");
 				
 				return cSearchItems;
 			}
 			//---
 			if ( (cTextKeywords != null) && (cTextKeywords.size() >= 1) )
        	{
        		for( Map.Entry<Double, String> cTextKeywordsEntry : cTextKeywords.entrySet())
        		{
        			String cTextKeyword = cTextKeywordsEntry.getValue();
        			
        			CategoryMO cCategory = 
        					new CategoryMO(ToolkitConstants.AMP_ALL_CATEGORY_VALUE);
         			
        			Source cSource = new Source();
        			cSource.setName(ToolkitConstants.AMP_AMAZON_SOURCE);
        			
        			cCategory.getcCategory().setSource(cSource);
        			
        			cCategory.getcCategory().setName(
        					ToolkitConstants.AMP_ALL_CATEGORY_VALUE);
        			
        			cCategory.getcCategory().setSearchindex(
        					ToolkitConstants.AMP_ALL_CATEGORY_VALUE);
        			
        			cCategory.getcCategory().setRootbrowsenode(
        					ToolkitConstants.AMP_ALL_CATEGORY_BROWSE_NODE);
         			
        			List<Items> cAmazonItems = 
        					this.getAmazonItems(cCategory, cTextKeyword, 1);
        			
        	        cSearchItems.addAll(cAmazonItems);
        		}
        	}
 			
 			this.printAmazonItems(cSearchItems);
 			
 			return cSearchItems;
 		} 
 		catch (Exception e) 
 		{
 			LOG.error(cMethodName + "::" + e.getMessage(), e);
 			
 			return new LinkedList<Items>();
 		}
 	}
    
    @SuppressWarnings("unchecked")
	public List<Items> getAmazonItems(CategoryMO cCategory,
    								  String cKeywords,	
    								  long cItemPage)
	{
		boolean cRes = true;
		
		String  cMethodName = "";
		
		List<Items> cSearchItems = new LinkedList<Items>();
		
		try
		{
			StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
	        StackTraceElement ste = stacktrace[1];
	        cMethodName = ste.getMethodName();
	
	        //-----------------------------------------
	        Map<String, Object> cMethodParams = new TreeMap<String, Object>();
	        Map<String, Object> cMethodResults = new TreeMap<String, Object>();
	        
	        Map<String, String> params = new HashMap<String, String>();
	        
	        if ( null == cCategory.getName() )
	        {
	        	String cMesage = cMethodName + "::cSerachIndex is null!";
	        	
	        	LOG.info(cMesage);
	        	
	        	
	        }
	        
	        if ( null == cCategory.getRootbrowsenode() )
	        {
	        	String cMesage = cMethodName + "::cBrowseNode is null!";
	        	
	        	LOG.info(cMesage);
	        	
	        	
	        }
	        //-----------------------------------------
	        if ( cRes )
	        {
	        	String cSerachIndex = cCategory.getSearchIndex();
	        	
	        	params.put("SearchIndex", cSerachIndex);
	 	        params.put("ResponseGroup", "Images,ItemAttributes,ItemIds,Offers,SalesRank");
	 	        //params.put("Sort", "salesrank");
	 	      
	 	        params.put("Keywords", cKeywords);
	 	        params.put("Availability", "Available");
	 	        params.put("ItemPage", String.valueOf(cItemPage));
	 	        params.put("Condition", "All");
	 	        
	 	        cMethodParams.put("p1", params);
	 	        
	 	        DataHandlerI cDataHandlerAWS = new DataHandlerAWS(this.cSystemConfiguration,
	 	        												  this.cThreadConfigurationMap);
	 	        
	 	        cRes = cDataHandlerAWS.handleItemSearchList(cMethodParams, cMethodResults);
	        }
	        //---
	        if ( cRes )
	        {
	        	cSearchItems =  (List<Items>)cMethodResults.get("r1");
	        	
	        	if ( null == cSearchItems )
	        	{
	        		String cMesage = cMethodName + "::Categories Nodes list is null!";
	        	
	        		LOG.info(cMesage);
	        	}
	        }
	        
	        return cSearchItems;
		}
		catch( Exception e)
		{
			LOG.error(cMethodName + "::" + e.getMessage(), e);
			
			return new LinkedList<Items>();
		}
	}
}
