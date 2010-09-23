/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.execution.registry;



import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import com.ipc.uda.service.execution.registry.ExecutionHandlerRegistryListener.ActionType;



/**
 * @author mordarsd
 * 
 */
class ExecutionHandlerConfig extends NotificationBroadcasterSupport implements ExecutionHandlerConfigMBean
{
    private String commandExecutionHandlerName;
    private String queryExecutionHandlerName;

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.execution.mbean.ExecutionHandlerConfigMBean#getCommandExecutionName()
     */
    @Override
    public String getCommandExecutionHandlerName()
    {
        return this.commandExecutionHandlerName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.execution.mbean.ExecutionHandlerConfigMBean#getQueryExecutionHandlerName()
     */
    @Override
    public String getQueryExecutionHandlerName()
    {
        return this.queryExecutionHandlerName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.execution.mbean.ExecutionHandlerConfigMBean#setCommandExecutionHandlerName(java.lang.String)
     */
    @Override
    public void setCommandExecutionHandlerName(String name)
    {
        this.commandExecutionHandlerName = name;
        this.sendNotification(ActionType.SET_COMMAND_HANDLER_NAME,name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.execution.mbean.ExecutionHandlerConfigMBean#setQueryExecutionHandlerName(java.lang.String)
     */
    @Override
    public void setQueryExecutionHandlerName(String name)
    {
        this.queryExecutionHandlerName = name;
        this.sendNotification(ActionType.SET_QUERY_HANDLER_NAME,name);
    }

    private void sendNotification(ActionType type, String name)
    {
        Notification notif = new Notification(type.name(),this,0,System.currentTimeMillis(),name);
        this.sendNotification(notif);
    }
}
