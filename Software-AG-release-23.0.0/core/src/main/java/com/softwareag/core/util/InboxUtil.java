package com.softwareag.core.util;

import com.adobe.granite.taskmanagement.Task;
import com.adobe.granite.taskmanagement.TaskManager;
import com.adobe.granite.taskmanagement.TaskManagerException;
import com.adobe.granite.workflow.exec.InboxItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class InboxUtil {

    private static final Logger LOG = LoggerFactory.getLogger(InboxUtil.class);

    public static final String NOTIFICATION_TASK_TYPE = "Notification";

    private InboxUtil() {
        // Class is not meant to be instantiated.
    }

    public static void addMessageToInbox(ResourceResolver resourceResolver, String assignee,
                                         String message, String title, String contentPath) throws TaskManagerException {
        if (resourceResolver != null && StringUtils.isNotBlank(assignee)) {
            final TaskManager taskManager = resourceResolver.adaptTo(TaskManager.class);
            if (taskManager != null) {
                Task newTask = taskManager.getTaskManagerFactory().newTask(NOTIFICATION_TASK_TYPE);
                newTask.setCurrentAssignee(assignee);
                newTask.setName(title);
                newTask.setContentPath(contentPath);
                newTask.setDescription(message);
                newTask.setPriority(InboxItem.Priority.HIGH);

                LOG.info("Sending Inbox Notification '{}' to '{}'", title, assignee);

                taskManager.createTask(newTask);
            } else {
                LOG.error("TaskManager must not be null.");
            }
        } else {
            LOG.error("ResourceResolver and assigne cannot be null.");
        }
    }
}
