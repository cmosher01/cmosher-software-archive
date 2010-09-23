/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service;

import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import weblogic.application.ApplicationContext;

import com.ipc.uda.service.lifecycle.LifecycleException;
import com.ipc.uda.service.lifecycle.LifecycleTask;
import com.ipc.uda.service.util.jmx.JMXUtil;
import com.ipc.uda.service.util.jmx.ObjectNameFactory;

/**
 * Application lifecycle task that configures the {@link com.ipc.uda.service.execution.registry.ExecutionHandlerRegistry ExecutionHandlerRegistry}.
 * @author mosherc
 * @author mordarsd
 */
public class RegisterDefaultExecutionHandlersTask implements LifecycleTask
{
    private static final String UDA_JMX_DOMAIN_NAME = "ipc";
    private static final String UDA_JMX_NAME = "uda";

    /**
     * Does nothing; we don't need to do anything at app-shutdown time. 
     */
    @Override
    public void doShutdown(final ApplicationContext app)
    {
        // nothing to do
    }

    /**
     * Registers the configured (in app params) execution handlers as the initial
     * handlers, in {@link com.ipc.uda.service.execution.registry.ExecutionHandlerRegistry ExecutionHandlerRegistry}.
     * This method is supposed to be called at app start-up time.
     * @see com.ipc.uda.service.execution.registry.ExecutionHandlerRegistry ExecutionHandlerRegistry
     */
    @Override
    public void doStartup(ApplicationContext app) throws LifecycleException
    {
        try
        {
            final MBeanServer server = JMXUtil.getLocalRuntimeMBeanServer();

            final ObjectName objName = ObjectNameFactory.create(UDA_JMX_DOMAIN_NAME,UDA_JMX_NAME,"ExecutionHandlerConfigMBean");
            
            final String commandExecutionHandler = app.getApplicationParameter("uda.defaults.CommandExecutionHandler");
            server.setAttribute(objName,new Attribute("CommandExecutionHandlerName",commandExecutionHandler));

            final String queryExecutionHandler = app.getApplicationParameter("uda.defaults.QueryExecutionHandler");
            server.setAttribute(objName,new Attribute("QueryExecutionHandlerName",queryExecutionHandler));
        }
        catch (final Throwable e)
        {
            throw new LifecycleException(e);
        }
    }

}
