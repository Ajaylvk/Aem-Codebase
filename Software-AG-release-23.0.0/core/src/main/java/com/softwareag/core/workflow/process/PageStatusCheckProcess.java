package com.softwareag.core.workflow.process;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

@Component(property = {
		Constants.SERVICE_DESCRIPTION + "=Write Adaptive Form Attachments to File System",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "= Page Activation Check"
	})
	public class PageStatusCheckProcess implements WorkflowProcess {

		private static final Logger log = LoggerFactory.getLogger(PageStatusCheckProcess.class);
	
		/*
		 * @Reference ResourceResolver resourceResolver;
		 */
		
		/*
		 * @Reference SendEmailService emailService;
		 * @Reference ServiceUser serviceUser;
		 */

		@Override
		public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap processArguments)
		throws WorkflowException {
			// TODO Auto-generated method stub
			String payloadPath = workItem.getWorkflowData().getPayload().toString();
			//log.info("payload info:::"+payloadPath);
			Map<String, String> map = new HashMap<String, String> ();
			
			//ResourceResolver resourceResolver = getServiceUserResolver();
			ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
			Resource payloadResource = resourceResolver.getResource(payloadPath+"/jcr:content");
			log.info("payload resource details:::"+payloadResource.getName()+"::path::"+payloadResource.getPath());
			ValueMap properties = ResourceUtil.getValueMap(payloadResource);
			String pageActivated = "false";
			 String lastModified = "";
			if(properties.containsKey("cq:lastReplicated")) { 
				
				String lastReplicated= properties.get("cq:lastReplicated", String.class);
				pageActivated = "true";
					 log.info("pageActivated::::"+pageActivated); //}
			}
				
			JSONArray jsonObject = getJsonFromFile(resourceResolver, "/conf/softwareag/contentapprovers.json");
			log.info("json object::::"+jsonObject);
			
			MetaDataMap wfd = workItem.getWorkflow().getWorkflowData().getMetaDataMap();

		    wfd.put("toList", jsonObject.toString());
		    wfd.put("pageActivated", pageActivated);
		    
		    Set<String> keyset = wfd.keySet();
		    Iterator<String> i = keyset.iterator();
		    while (i.hasNext()){
		     Object key = i.next();
		     log.info("The workflow medata includes key {} and value {}",key.toString(),wfd.get(key).toString());
		    }
		   
		}
		
		public JSONArray getJsonFromFile(ResourceResolver resolver,String filePath){
			JSONArray jsonObj=new JSONArray();
			    Resource resource=  resolver.getResource(filePath);
			   // log.info("resource details:::"+resource.getName()+"::path::"+resource.getPath());
			    try {
			        Node jcnode = resource.adaptTo(Node.class).getNode("jcr:content");
			       // log.info("jcrnode:::::"+jcnode.getName()+":::path:::"+jcnode.getPath());
			        InputStream content=jcnode.getProperty("jcr:data").getBinary().getStream();
			        StringBuilder sb = new StringBuilder();
			        String line;            
			        BufferedReader br = new BufferedReader(new 
			        InputStreamReader(content,StandardCharsets.UTF_8));
			        while ((line = br.readLine()) != null) {
			            sb.append(line);
			        }
			       // log.info("buffered reader :::"+sb.toString());
			        jsonObj = new JSONArray(sb.toString());

			    }catch (Exception e) {
			        log.error(e.getMessage(),e);
			    }

			    return jsonObj;
			}
		
		@Reference
		ResourceResolverFactory resourceResolverFactory;
		
	/*	public ResourceResolver getServiceUserResolver() {
			// TODO Auto-generated method stub
			
			//sagserviceuser
			Map<String, Object> param = new HashMap<String, Object>();
			param.put(ResourceResolverFactory.SUBSERVICE, "sagserviceuser");
			ResourceResolver resolver = null;
				try {
					resolver = resourceResolverFactory.getServiceResourceResolver(param);
				} catch (LoginException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return resolver;
		} */
}
