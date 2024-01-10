package com.softwareag.core.workflow.process;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.Externalizer;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component(property = {
		Constants.SERVICE_DESCRIPTION + "=Write Adaptive Form Attachments to File System",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "= Reject Notification Process Step"
	})
public class RejectNotificationProcess implements WorkflowProcess {

  private static Logger log = LoggerFactory.getLogger(EmailNotificationProcess.class);

  @Reference
  private MessageGatewayService messageGatewayService;

  @Reference
  private Externalizer externalizer;
  
	/*
	 * @OSGiService SlingHttpServletRequest request;
	 */
  
	/*
	 * @Reference EmailService emailService;
	 */
  
  private void sendEmail(String subjectLine, String msgBody, String[] recipients) throws EmailException {
    Email email = new HtmlEmail();
    for (String recipient : recipients) {
      email.addTo(recipient, recipient);
    }
    email.setSubject(subjectLine);
    email.setMsg(msgBody);
    MessageGateway<Email> messageGateway = messageGatewayService.getGateway(HtmlEmail.class);
    if (messageGateway != null) {
      log.info("sending out email");
      messageGateway.send((Email) email);
    } else {
      log.info("The message gateway could not be retrieved.");
    }
  }

	@Override
	public void execute(WorkItem workItem, WorkflowSession session, MetaDataMap map) throws WorkflowException {
		// TODO Auto-generated method stub
		
		JSONObject jsonResponse = new JSONObject();
	    boolean sent = false;
	    
	    try {
	    	String pagePath = workItem.getWorkflowData().getPayload().toString();
	    	String approvalStatus = null;
	    	String comments = null;
	    	String assignGroup = null;
	    	if(workItem.getWorkflowData().getMetaDataMap().containsKey("approvalStatus")) {
	    		approvalStatus = workItem.getWorkflowData().getMetaDataMap().get("approvalStatus").toString();
	    	}
	    	if(workItem.getWorkflowData().getMetaDataMap().containsKey("comment")) {
	    		comments = workItem.getWorkflowData().getMetaDataMap().get("comment").toString();
	    		
	    	}
	    	// Read comments
	    	List<HistoryItem> history = session.getHistory(workItem.getWorkflow());
	        if(!history.isEmpty()){
	            HistoryItem last = history.get(history.size() - 1);
	            if(StringUtils.isNotBlank(last.getComment())){
	            	comments = last.getComment();
	            	assignGroup = last.getWorkItem().getCurrentAssignee();
	            	log.debug("Rejected comments::::"+last.getComment());
	            }
	        }
	        
	    	//Read comments End
	    	ResourceResolver resourceResolver = session.adaptTo(ResourceResolver.class);
	    	String authorUrl = externalizer.authorLink(resourceResolver, "/editor.html"+pagePath +".html");
			/*
			 * String url = pagePath +".html"; String authorRequestUrl =
			 * externalizer.absoluteLink(request, request.getScheme(), url);
			 * log.info("authorRequestUrl:::::::"+authorRequestUrl);
			 */
	    	
            UserManager manager = resourceResolver.adaptTo(UserManager.class);

            Authorizable authorizable = manager.getAuthorizable(workItem.getWorkflow().getInitiator());
			
            Value[] initiatorEmail = authorizable.getProperty("./profile/email");
            List<String> recipientsList = new ArrayList();
            for (final Value email : initiatorEmail) {
            	recipientsList.add(email.getString());
           // log.debug("email id :::::::"+email.getString());
            }
	    
    	    String[] recipients = recipientsList.toArray(String[]::new);
    	    StringBuilder builder = new StringBuilder();
	    	builder.append("Hello Editor,<br><br><br> Your activation request for the page [0] has been rejected by the "+ assignGroup+" Group with the comment(s) [1] mentioned below. Kindly re-initiate the page publishing/activation process once the authoring/editing is completed.<br>");
	    	builder.append("<br><br>[0]: <a href=\""+authorUrl+"\">"+authorUrl+"</a>");
	    	builder.append("<br><br>[1]: Comments:"+comments);
	    	builder.append("<br><br><br> Disclaimer: This is an auto-generated email. Kindly do not respond.<br><br><br>Thanks,\r\n Growth Marketing Global<br>");
	    	log.info("html content ::::"+builder.toString());
	    	sendEmail("Content Reject Notification", builder.toString(), recipients);
	     // sendEmail("Reject content email",
	       // "This is the email content Rejected "+authorUrl, recipients);
	      sent = true;
	    } catch (EmailException | RepositoryException  e) {
	    	e.printStackTrace();
	    	log.error("emailexception:::",e.getStackTrace());
	    	log.error("emailexception message:::",e.getMessage());
	    }
	     // jsonResponse.put("result", sent ? "done" : "something went wrong");
	      log.info("email response:::: jsonResponse"+jsonResponse);    
		
	}

}


