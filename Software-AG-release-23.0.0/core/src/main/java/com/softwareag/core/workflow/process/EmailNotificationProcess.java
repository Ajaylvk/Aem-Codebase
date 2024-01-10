package com.softwareag.core.workflow.process;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.mail.MessagingException;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
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
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.mail.MailTemplate;
import com.day.cq.mailer.MessageGateway;
import com.day.cq.mailer.MessageGatewayService;

@Component(property = {
		Constants.SERVICE_DESCRIPTION + "=Write Adaptive Form Attachments to File System",
		Constants.SERVICE_VENDOR + "=Adobe Systems",
		"process.label" + "= Email Notification Process Step"
	})
public class EmailNotificationProcess implements WorkflowProcess {

  private static Logger log = LoggerFactory.getLogger(EmailNotificationProcess.class);

  private static final String TEMPLATE_PATH = "/conf/softwareag/emailtemplates/approvalnotification.html";
  @Reference
  private MessageGatewayService messageGatewayService;

  @Reference
  private Externalizer externalizer;
  
  private void sendEmail(String subjectLine, String msgBody, String[] recipients) throws EmailException, IOException, MessagingException {
	  
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
	    
	    try(ResourceResolver resourceResolver = session.adaptTo(ResourceResolver.class)) {
	    	String toList = workItem.getWorkflowData().getMetaDataMap().get("toList").toString();
	    	String pagePath = workItem.getWorkflowData().getPayload().toString();
	    	//ResourceResolver resourceResolver = session.adaptTo(ResourceResolver.class);
	    	
	    	String authorUrl = externalizer.authorLink(resourceResolver, "/editor.html"+pagePath +".html");
	    	log.debug("author url:::"+authorUrl);
	    	String url = pagePath +".html";
			/*
			 * String authorRequestUrl = externalizer.absoluteLink(request,
			 * request.getScheme(), url);
			 * log.info("authorRequestUrl:::::::"+authorRequestUrl);
			 */
	    	// Editor details :::::
	    	String editorName = workItem.getWorkflow().getInitiator();
	    	log.debug("editor name;::::::"+editorName);
	    	UserManager manager = resourceResolver.adaptTo(UserManager.class);
	    	String editorEmail = null;
            Authorizable authorizable = manager.getAuthorizable(workItem.getWorkflow().getInitiator());
			
            Value[] initiatorEmail = authorizable.getProperty("./profile/email");
            for (final Value email : initiatorEmail) {
            	editorEmail = email.getString();
            }
            // Editor details end
	    	//String absoluteUrl = externalizer.absoluteLink(request, request.getScheme(), "/editor.html"+pagePath+".html");
	    	//log.info("absolute URL::::"+absoluteUrl);
	    	//externalizer.absoluteLink(resourceResolver, pagePath, authorUrl)
            
            // To list from group 
            List<String> recipeientsEmail = new ArrayList<String>();
            Group group = (Group) manager.getAuthorizable("softwareag-aem-content-approver");
            log.info("group >>> "+group);
          final Iterator<Authorizable> memberslist = group.getMembers();
                  while (memberslist.hasNext()) {
                  final Authorizable user = memberslist.next();
                         if (user != null && !user.isGroup() && user.getProperty("./profile/email") != null) {
                                //EMAIL ADDRESS FOR MEMBERS
                                Value[] usrEmail = user.getProperty("./profile/email");
                                for(Value emailGroup : usrEmail) {
                                       log.info("emailGroup line 152>>> "+emailGroup);
                                       recipeientsEmail.add(emailGroup.toString());
                                }
                         }
                  }
            
            // to list end
	    //	List<String> recipientsList = getToListData(toList);
	    	String[] recipients = recipeientsEmail.toArray(String[]::new);
	    	StringBuilder builder = new StringBuilder();
	    	builder.append("Hello Approver,<br><br><br> A page activation is initiated by "+ editorName+"("+editorEmail+") on the website. Requesting you to kindly review the page [0] and provide your approval/rejection.<br>");
	    	builder.append("<br>[0]: <a href=\""+authorUrl+"\">"+authorUrl+"</a>");
	    	builder.append("<br><br><br> Disclaimer: This is an auto-generated email. Kindly do not respond.<br><br><br>Thanks,\r\n Growth Marketing Global<br>");
	    	log.info("html content ::::"+builder.toString());
	    	sendEmail("Content Approval Request Notification", builder.toString(), recipients);
	    	log.info("Email sent successfully::::::");
	    } catch (EmailException | IOException | MessagingException | RepositoryException e) {
	    	e.printStackTrace();
	    	log.error("emailexception:::",e.getStackTrace());
	    	log.error("emailexception message:::",e.getMessage());
	    }
	     // jsonResponse.put("result", sent ? "done" : "something went wrong");
	      log.info("email response:::: jsonResponse"+jsonResponse);    
	}

	/*
	 * private List<String> getToListData(String toList) { // TODO Auto-generated
	 * method stub List<String> emailToList = new ArrayList<>(); JSONArray array;
	 * try { array = new JSONArray(toList); for(int i=0; i < array.length(); i++) {
	 * JSONObject object = array.getJSONObject(i);
	 * log.info("email value read form json:::"+object.getString("email"));
	 * emailToList.add(object.getString("email"));
	 * 
	 * }
	 * 
	 * } catch (JSONException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } return emailToList; }
	 */
	
	 private MailTemplate getMailTemplate(String templatePath, ResourceResolver resourceResolver) throws IllegalArgumentException {
	        MailTemplate mailTemplate = null;
	        mailTemplate = MailTemplate.create(templatePath, resourceResolver.adaptTo(Session.class));

			if (mailTemplate == null) {
			    throw new IllegalArgumentException("Mail template path [ "
			            + templatePath + " ] could not resolve to a valid template");
			}
			log.info("mail Template :::::"+mailTemplate.toString());
	        return mailTemplate;
	    }
	
}


