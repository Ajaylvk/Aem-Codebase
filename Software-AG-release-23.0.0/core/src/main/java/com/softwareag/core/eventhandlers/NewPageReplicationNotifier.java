package com.softwareag.core.eventhandlers;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.*;

@Component(immediate = true, service = EventHandler.class, property = {
        Constants.SERVICE_DESCRIPTION + "= This event handler listens the events new page creation and replication event,then notifies users",
        EventConstants.EVENT_TOPIC + "="+ PageEvent.EVENT_TOPIC,
        EventConstants.EVENT_TOPIC +"="+ ReplicationAction.EVENT_TOPIC,
        EventConstants.EVENT_FILTER + "(path=/content/softwareag))" })
public class NewPageReplicationNotifier implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(NewPageReplicationNotifier.class);
    @Reference
    private JobManager jobManager;
    @Reference
    private ResourceResolverFactory rrFactory;
    private ResourceResolver resourceResolver;

    private static final HashSet<String> newPages = new HashSet<>();

    @Override
    public void handleEvent(Event event) {
        Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, "sag-replicationnotification");
        try {
            resourceResolver = rrFactory.getServiceResourceResolver(params);
            Session session = resourceResolver.adaptTo(Session.class);
            Node parentNode = session.getNode("/conf");
            if (parentNode.hasNode("newPages")) {
                Node newPagePaths = parentNode.getNode("newPages");
                List<Value> values = Arrays.asList(newPagePaths.getProperty("newPages").getValues());
                for (Value value : values) {
                    newPages.add(value.getString());
                }
            }
        } catch (LoginException | RepositoryException e) {
            LOG.debug("Exception Occurred:::" + e);
        }
        if (event.getTopic().equals(PageEvent.EVENT_TOPIC)) {
            Iterator<PageModification> pageInfo = PageEvent.fromEvent(event).getModifications();
            while (pageInfo.hasNext()) {
                PageModification pageModification = pageInfo.next();
                String pagePath = pageModification.getPath();
                if (pageModification.getType().equals(PageModification.ModificationType.CREATED)) {

                   if(pagePath.contains("/content/softwareag/"))

                    if(pagePath.contains("/content/softwareag/"))

                    newPages.add(pagePath);
                }
                if (pageModification.getType().equals(PageModification.ModificationType.DELETED)) {
                    if (newPages.contains(pagePath))
                        newPages.remove(pagePath);
                }
            }
            try {
                Session session = resourceResolver.adaptTo(Session.class);
                Node parentNode = session.getNode("/conf");
                if (parentNode.hasNode("newPages")) {
                    updateNode(parentNode,session,newPages);
                } else {
                    createNode(parentNode,session);
                }
            } catch (RepositoryException e) {
                LOG.debug("Exception Occurred:::" + e);
            }
            LOG.debug("The pages in the list are:" + newPages.toString());
        }
        if (event.getTopic().equals(ReplicationAction.EVENT_TOPIC)) {
            if (ReplicationAction.fromEvent(event).getType().equals(ReplicationActionType.ACTIVATE)) {
                Map<String, Object> jobProperties = new HashMap<String, Object>();
                LOG.debug("There you go a page is activated and capturing it:" + ReplicationAction.fromEvent(event).getPath());
                if (newPages.contains(ReplicationAction.fromEvent(event).getPath())) {
                    newPages.remove(ReplicationAction.fromEvent(event).getPath());
                    try {
                        Session session = resourceResolver.adaptTo(Session.class);
                        Node parentNode = session.getNode("/conf");
                        updateNode(parentNode, session, newPages);
                        jobProperties.put("currentPage", ReplicationAction.fromEvent(event).getPath());
                        jobProperties.put("editorName", ReplicationAction.fromEvent(event).getUserId());
                        this.jobManager.addJob("sendEmail/job", jobProperties);
                    }catch (RepositoryException e){
                        LOG.debug("Exception Occurred:::" + e);
                    }
                }
            }
        }
    }
    public static void createNode(Node parentNode, Session session) {
        try {
            Node newPagePaths = parentNode.getNode("newPages");
            String[] strArray = new String[newPages.size()];
            newPagePaths.setProperty("newPages", newPages.toArray(strArray));
            session.save();
        } catch (RepositoryException e) {
            LOG.debug("Exception Occurred:::" + e);
        }
    }
    public static void updateNode(Node parentNode,Session session,Set<String> newPages) {
        try {
            Node newPagePaths = parentNode.getNode("newPages");
            String[] strArray = new String[newPages.size()];
            newPagePaths.setProperty("newPages", newPages.toArray(strArray));
            session.save();
        } catch (RepositoryException e) {
            LOG.debug("Exception Occurred:::" + e);
        }
    }
}

