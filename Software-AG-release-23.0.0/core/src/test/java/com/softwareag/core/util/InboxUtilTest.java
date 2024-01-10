package com.softwareag.core.util;

import com.adobe.granite.taskmanagement.Task;
import com.adobe.granite.taskmanagement.TaskManager;
import com.adobe.granite.taskmanagement.TaskManagerException;
import com.adobe.granite.taskmanagement.TaskManagerFactory;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InboxUtilTest {

    @Test
    public void addMessageToInbox_fails_ifAssigneeIsNull() throws TaskManagerException {
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        InboxUtil.addMessageToInbox(resourceResolver,null, null, null, null);

        verify(resourceResolver, times(0)).adaptTo(TaskManager.class);
    }

    @Test
    public void testAddMessageToInbox() throws TaskManagerException {
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        TaskManager taskManager = mock(TaskManager.class);
        TaskManagerFactory taskManagerFactory = mock(TaskManagerFactory.class);
        Task task = mock(Task.class);

        when(resourceResolver.adaptTo(TaskManager.class)).thenReturn(taskManager);
        when(taskManager.getTaskManagerFactory()).thenReturn(taskManagerFactory);
        when(taskManagerFactory.newTask(InboxUtil.NOTIFICATION_TASK_TYPE)).thenReturn(task);

        InboxUtil.addMessageToInbox(resourceResolver,"admin", "message", "title", "content/path");

        verify(taskManager, times(1)).createTask(task);
    }
}
