/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.locator;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import weblogic.application.ApplicationContext;

import com.ipc.uda.service.lifecycle.LifecycleException;
import com.ipc.uda.service.lifecycle.LifecycleTask;
import com.ipc.uda.service.locator.mbean.ServiceLocatorConfig;
import com.ipc.uda.service.util.jmx.JMXUtil;
import com.ipc.uda.service.util.jmx.ObjectNameFactory;

/**
 * @author mordarsd
 *
 */
public abstract class AbstractServiceLocatorLifecycleTask implements LifecycleTask, NotificationListener
{
    
   
    private final ObjectName cfgObjectName;
    private ServiceLocatorConfig svcLocatorCfg;
    
    protected AbstractServiceLocatorLifecycleTask(final String mbeanName)
    {
        try
        {
            cfgObjectName = ObjectNameFactory.create("ipc", "uda", mbeanName);
        }
        catch (final MalformedObjectNameException e)
        {
            // This is a programming error; check the arguments to
            // ObjectNameFactory.create and make sure they are valid.
            throw new IllegalStateException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.ipc.uda.service.lifecycle.LifecycleTask#doShutdown(weblogic.application.ApplicationContext)
     */
    @Override
    public void doShutdown(ApplicationContext app) throws LifecycleException
    {
        this.tryDeRegisterMBeanListener();
    }

    /* (non-Javadoc)
     * @see com.ipc.uda.service.lifecycle.LifecycleTask#doStartup(weblogic.application.ApplicationContext)
     */
    @Override
    public void doStartup(ApplicationContext app) throws LifecycleException
    {
        
        
        final String providerHostnameParam = this.getProviderHostnameParam();
        final String providerPortParam = this.getProviderPortParam();
        
        final String hostName = app.getApplicationParameter(providerHostnameParam);
        final String portStr = app.getApplicationParameter(providerPortParam);

        if (hostName == null || portStr == null)
        {
            throw new LifecycleException(
                    "Unable to perform lifecycle startup of ServiceLocatorProvider. Either "
                    + providerHostnameParam + " and/or " + providerPortParam + " was null. "
                    + "Check the weblogic-application.xml Deployment Descriptor. ");
        }

        final int port = Integer.parseInt(portStr);

        svcLocatorCfg = new ServiceLocatorConfig(hostName, port);
        
        this.tryRegisterMBeanListener(svcLocatorCfg);
        
        this.configureServiceLocator(svcLocatorCfg);
    }
    
    /**
     * 
     * @return host name parameter name
     */
    protected abstract String getProviderHostnameParam();
    
    /**
     * 
     * @return port parameter name
     */
    protected abstract String getProviderPortParam();
    
    /**
     * 
     * @param cfg
     */
    protected abstract void configureServiceLocator(ServiceLocatorConfig cfg);
    
    private void tryRegisterMBeanListener(final ServiceLocatorConfig cfg)
    {
        try
        {
            final MBeanServer server = JMXUtil.getLocalRuntimeMBeanServer();
            if (server.isRegistered(cfgObjectName))
            {
                server.unregisterMBean(cfgObjectName);
            }

            server.registerMBean(cfg, cfgObjectName);

            server.addNotificationListener(cfgObjectName, this, null, null);
        }
        catch (final Exception e)
        {
            throw new IllegalStateException(
                    "Unexpected Exception caught while attempting to Register " +
                    "MBean Listener for ObjectName: " + this.cfgObjectName);
        }
    }
    
    private void tryDeRegisterMBeanListener()
    {
        try
        {
            final MBeanServer server = JMXUtil.getLocalRuntimeMBeanServer();

            server.removeNotificationListener(cfgObjectName, this);

            if (server.isRegistered(cfgObjectName))
            {
                server.unregisterMBean(cfgObjectName);
            }

        }
        catch (final Exception e)
        {
            throw new IllegalStateException(
                    "Unexpected Exception caught while attempting to DeRegister " +
                    "MBean Listener for ObjectName: " + this.cfgObjectName);
        }
    }

    @Override
    public void handleNotification(Notification notification, Object handback)
    {
        System.out.println( "Setting new ServiceLocation to: " + notification.getSource());
        
        final Object cfg = notification.getUserData();
        if ((cfg != null) && (cfg instanceof ServiceLocatorConfig))
        {
            this.configureServiceLocator((ServiceLocatorConfig)cfg);
        }
        
    }
    

}
