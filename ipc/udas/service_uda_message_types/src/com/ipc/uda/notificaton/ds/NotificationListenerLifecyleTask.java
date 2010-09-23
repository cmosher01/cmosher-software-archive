/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.notificaton.ds;



import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import weblogic.application.ApplicationContext;

import com.ipc.uda.event.notification.ds.NotificationListener;
import com.ipc.uda.event.notification.ds.UdaNotifiable;
import com.ipc.uda.service.lifecycle.LifecycleException;
import com.ipc.uda.service.lifecycle.LifecycleTask;
import com.ipc.uda.service.util.NamingUtil;
import com.ipc.uda.service.util.UdaEnvironmentUtil;



/**
 * @author mordarsd
 * 
 */
public final class NotificationListenerLifecyleTask implements LifecycleTask
{

    
    private final Set<NotificationListener> listenerSet = new HashSet<NotificationListener>(Arrays
            .<NotificationListener> asList(new NotificationListener[] { new ContactNotificationListener(), }));

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.lifecycle.LifecycleTask#doShutdown(weblogic.application.ApplicationContext)
     */
    @Override
    public void doShutdown(ApplicationContext app) throws LifecycleException
    {
        try
        {
            // Remove the UdaNotifiable from the JNDI tree
            this.removeNotifiableFromJndiTree();
            
            for (final NotificationListener listener : this.listenerSet)
            {
                UdaNotifiable.getInstance().unregisterNotificationListener(listener);
            }
        }
        catch (Exception e)
        {
            throw new LifecycleException(
                    "Unexpected Exception caught while attempting to perform " +
                    "NotificationListenerLifecyleTask::doShutdown()", e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ipc.uda.service.lifecycle.LifecycleTask#doStartup(weblogic.application.ApplicationContext)
     */
    @Override
    public void doStartup(ApplicationContext app) throws LifecycleException
    {
        try
        {
            // Add the UdaNotifiable to the JNDI tree
            this.addNotifiableToJndiTree();
            
            for (final NotificationListener listener : this.listenerSet)
            {
                UdaNotifiable.getInstance().registerNotificationListener(listener);
            }
        }
        catch (Exception e)
        {
            throw new LifecycleException(
                    "Unexpected Exception caught while attempting to perform " +
                    "NotificationListenerLifecyleTask::doStartup()", e);
        }
    }

    private void addNotifiableToJndiTree() throws NamingException
    {
        Context ctx = null;
        Context ctxSub = null;
        try
        {
            ctx = new InitialContext();
            ctxSub = ctx.createSubcontext(UdaNotifiable.JNDI_SUB_CTX);
            ctxSub.bind(UdaNotifiable.JNDI_NAME,UdaNotifiable.getInstance());
        }
        finally
        {
            NamingUtil.closeContext(ctxSub);
            NamingUtil.closeContext(ctx);
        }
    }
    
    private void removeNotifiableFromJndiTree() throws NamingException
    {
        Context ctx = null;
        try
        {
            ctx = new InitialContext();
            ctx.unbind(UdaNotifiable.JNDI_FULL_NAME);
            ctx.destroySubcontext(UdaNotifiable.JNDI_SUB_CTX);
        }
        finally
        {
            NamingUtil.closeContext(ctx);
        }
    }
    

}
