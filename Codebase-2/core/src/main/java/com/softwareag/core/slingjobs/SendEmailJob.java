package com.softwareag.core.slingjobs;

import com.adobe.acs.commons.email.EmailService;
import com.day.cq.commons.Externalizer;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Value;
import java.util.*;

@Component(service = JobConsumer.class,immediate = true, property = {
        JobConsumer.PROPERTY_TOPICS + "=sendEmail/job"
        })
public class SendEmailJob implements JobConsumer {

        private static final Logger LOG = LoggerFactory.getLogger(SendEmailJob.class);
        @Reference
        private ResourceResolverFactory rrFactory;
        private ResourceResolver resourceResolver;
        @Reference
        private Externalizer externalizer;
        private static final String TEMPLATE_PATH = "/etc/notification/email/SAG-Emailtemplates/replication-notification.txt";
        @Reference
        private EmailService emailService;
        @Reference
        private SlingSettingsService slingSettingsService;

        @Override
        public JobResult process(Job job) {
             try{
                 String editorName= (String)job.getProperty("editorName");
                 String currentPage= (String)job.getProperty("currentPage");
                 Map<String, Object> params = new HashMap<>();
                 params.put(ResourceResolverFactory.SUBSERVICE, "sag-replicationnotification");
                 resourceResolver = rrFactory.getServiceResourceResolver(params);
                 List<String> recipientsEmail = new ArrayList<String>();
                 UserManager manager = resourceResolver.adaptTo(UserManager.class);
                 String editorEmail = "";
                 Authorizable authorizable = Objects.requireNonNull(manager).getAuthorizable(editorName);
                 Value[] initiatorEmail = Objects.requireNonNull(authorizable).getProperty("./profile/email");
                 for (final Value email : initiatorEmail) {
                     editorEmail = email.getString();
                 }
                 Group group = (Group) manager.getAuthorizable("softwareag-aem-content-approver");
                 LOG.debug("group >>> "+group);
                 final Iterator<Authorizable> membersList = Objects.requireNonNull(group).getMembers();
                 while (membersList.hasNext()) {
                     final Authorizable user = membersList.next();
                     if (user != null && !user.isGroup() && user.getProperty("./profile/email") != null) {
                         Value[] usrEmail = user.getProperty("./profile/email");
                         for(Value emailGroup : usrEmail) {
                             recipientsEmail.add(emailGroup.toString());
                         }
                     }
                 }
                 String authorUrl="";
                 if(this.slingSettingsService.getRunModes().contains("stage")) {
                     authorUrl = externalizer.externalLink(resourceResolver, "publish",currentPage + ".html");
                 }
                 else{
                     authorUrl = externalizer.externalLink(resourceResolver, "sag",currentPage + ".html");
                 }
                 LOG.info("recipients list"+recipientsEmail.toString());
                 Map<String, String> emailParams = new HashMap<>();
                 emailParams.put("editorName", editorName);
                 emailParams.put("editorEmail", editorEmail);
                 emailParams.put("authorUrl", authorUrl);
                 emailService.sendEmail(TEMPLATE_PATH,emailParams,recipientsEmail.toArray(new String[] {}));
                 LOG.debug("Email sent successfully::::::");
                 return JobResult.OK;
             }catch (Exception e){
                 LOG.debug("Exception Occurred:::"+ e);
                 return JobResult.FAILED;
             }
        }

}
