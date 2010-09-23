/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service;



import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import com.ipc.uda.service.locator.ServiceLocation;
import com.ipc.uda.service.locator.mbean.ServiceLocatorConfig;
import com.ipc.uda.service.util.logging.Log;



/**
 * @author mordarsd
 * 
 */
public final class EJBHomeLocator
{
    private static EJBHomeLocator instance = new EJBHomeLocator();
    private InitialContext localInitialCtx;

    private Map<String, EJBLocalHome> localHomeCache = new HashMap<String, EJBLocalHome>();

    public static EJBHomeLocator getInstance()
    {
        return instance;
    }

    private EJBHomeLocator()
    {
        try
        {
            localInitialCtx = new InitialContext();
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 
     * 
     * @param jndiName
     * @param serviceLocatorConfig
     * 
     * @return
     * 
     * @throws NamingException
     * @throws ClassCastException
     */
    public synchronized EJBHome getRemoteHome(final String jndiName, final ServiceLocatorConfig serviceLocatorConfig)
            throws NamingException, ClassCastException
    {
        return getRemoteHome(jndiName, serviceLocatorConfig, null, null, null);
    }

    /**
     * 
     * 
     * @param jndiName
     * @param serviceLocatorConfig
     * @param authType
     * @param principal
     * @param credentials
     * 
     * @return
     * 
     * @throws NamingException
     * @throws ClassCastException
     */
    public synchronized EJBHome getRemoteHome(final String jndiName, final ServiceLocatorConfig serviceLocatorConfig,
            final String authType, final String principal, final String credentials) throws NamingException, ClassCastException
    {
        EJBHome home = null;

        InitialContext initialCtx = null;
        try
        {
            final String providerUrl = "t3://" + serviceLocatorConfig.getHostName() + ":" + serviceLocatorConfig.getPort();

            Hashtable<String, String> env = new Hashtable<String, String>(5);
            env.put(Context.INITIAL_CONTEXT_FACTORY,"weblogic.jndi.WLInitialContextFactory");

            if ((authType != null) && (authType.length() > 0))
            {
                env.put(Context.SECURITY_AUTHENTICATION,authType);
            }
            else
            {
                env.put(Context.SECURITY_AUTHENTICATION,"none");
            }

            if ((principal != null) && (principal.length() > 0))
            {
                env.put(Context.SECURITY_PRINCIPAL,principal);
            }
            if ((credentials != null) && (credentials.length() > 0))
            {
                env.put(Context.SECURITY_CREDENTIALS,credentials);
            }

            env.put(Context.PROVIDER_URL,providerUrl);

            initialCtx = new InitialContext(env);
            final Object obj = initialCtx.lookup(jndiName);
            home = (EJBHome)PortableRemoteObject.narrow(obj,EJBHome.class);
        }
        finally
        {
            if (initialCtx != null)
            {
                try
                {
                    initialCtx.close();
                }
                catch (NamingException ne)
                {
                    Log.logger().info("error closing context; ignoring it:",ne);
                }
            }
        }

        return home;
    }

    public synchronized EJBHome getRemoteHome(final String jndiName, final Hashtable<?, ?> ctxProps) throws NamingException,
            ClassCastException
    {
        EJBHome home = null;

        InitialContext initialCtx = null;
        try
        {
            initialCtx = new InitialContext(ctxProps);
            final Object obj = initialCtx.lookup(jndiName);
            home = (EJBHome)PortableRemoteObject.narrow(obj,EJBHome.class);
        }
        finally
        {
            if (initialCtx != null)
            {
                try
                {
                    initialCtx.close();
                }
                catch (NamingException ne)
                {
                    Log.logger().info("error closing context; ignoring it:",ne);
                }
            }
        }

        return home;
    }

    public synchronized EJBLocalHome getLocalHome(final String jndiName) throws NamingException, ClassCastException
    {

        EJBLocalHome home = null;

        if (this.localHomeCache.containsKey(jndiName))
        {
            home = this.localHomeCache.get(jndiName);
        }
        else
        {
            // InitialContext ctx = null;
            try
            {
                // ctx = new InitialContext();
                final Object obj = localInitialCtx.lookup(jndiName);
                home = (EJBLocalHome)obj;
            }
            finally
            {
                // if (ctx != null)
                // {
                // try
                // {
                // ctx.close();
                // } catch (final Throwable ignore)
                // {
                // Log
                // .logger()
                // .info(
                // "Exception occurred while closing JNDI InitialContext; ignoring it.",
                // ignore);
                // }
                // }
            }
            this.localHomeCache.put(jndiName,home);
        }

        return home;
    }
}
