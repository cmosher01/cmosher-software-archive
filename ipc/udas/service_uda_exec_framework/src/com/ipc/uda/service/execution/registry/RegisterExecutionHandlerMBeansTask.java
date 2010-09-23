/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.execution.registry;



import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import weblogic.application.ApplicationContext;

import com.ipc.uda.service.lifecycle.LifecycleException;
import com.ipc.uda.service.lifecycle.LifecycleTask;
import com.ipc.uda.service.util.jmx.JMXUtil;
import com.ipc.uda.service.util.jmx.ObjectNameFactory;



/**
 * @author mordarsd
 * 
 */
public class RegisterExecutionHandlerMBeansTask implements LifecycleTask
{
    private static final ObjectName EXE_HANDLER_CFG_MBEAN_OBJ_NAME;
    static
    {
        try
        {
            EXE_HANDLER_CFG_MBEAN_OBJ_NAME = ObjectNameFactory.create("ipc","uda","ExecutionHandlerConfigMBean");
        }
        catch (final MalformedObjectNameException e)
        {
            // This is a programming error; check the arguments to
            // ObjectNameFactory.create and make sure they are valid.
            throw new IllegalStateException(e);
        }
    }
    private static final ExecutionHandlerRegistryListener listener = new ExecutionHandlerRegistryListener(ExecutionHandlerRegistry.getInstance());





    @Override
    public void doStartup(final ApplicationContext app) throws LifecycleException
    {
        try
        {
            final MBeanServer server = JMXUtil.getLocalRuntimeMBeanServer();
            if (server.isRegistered(EXE_HANDLER_CFG_MBEAN_OBJ_NAME))
            {
                server.unregisterMBean(EXE_HANDLER_CFG_MBEAN_OBJ_NAME);
            }

            server.registerMBean(new ExecutionHandlerConfig(),EXE_HANDLER_CFG_MBEAN_OBJ_NAME);

            server.addNotificationListener(EXE_HANDLER_CFG_MBEAN_OBJ_NAME,RegisterExecutionHandlerMBeansTask.listener,null,null);
        }
        catch (final Throwable e)
        {
            throw new LifecycleException(e);
        }
    }

    @Override
    public void doShutdown(final ApplicationContext app) throws LifecycleException
    {
        try
        {
            final MBeanServer server = JMXUtil.getLocalRuntimeMBeanServer();

            server.removeNotificationListener(EXE_HANDLER_CFG_MBEAN_OBJ_NAME,RegisterExecutionHandlerMBeansTask.listener);

            if (server.isRegistered(EXE_HANDLER_CFG_MBEAN_OBJ_NAME))
            {
                server.unregisterMBean(EXE_HANDLER_CFG_MBEAN_OBJ_NAME);
            }

        }
        catch (final Throwable e)
        {
            throw new LifecycleException(e);
        }
    }
}
