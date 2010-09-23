/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.execution.registry;



/**
 * @author mordarsd
 * 
 */
public interface ExecutionHandlerConfigMBean
{

    /**
     * 
     * @param name
     */
    void setCommandExecutionHandlerName(String name);

    /**
     * 
     * @param name
     */
    void setQueryExecutionHandlerName(String name);

    /**
     * 
     * @return
     */
    String getCommandExecutionHandlerName();

    /**
     * 
     * @return
     */
    String getQueryExecutionHandlerName();

}
