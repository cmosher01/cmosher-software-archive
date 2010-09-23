/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.entity.test.manager;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ipc.ds.base.exception.StorageFailureException;
import com.ipc.uda.entity.dto.UserContextTransient;
import com.ipc.uda.entity.internal.dto.impl.UserContextTransientImpl;
import com.ipc.uda.entity.manager.UserContextTransientManager;
import com.ipc.uda.event.notification.ds.UdaNotifiable;
import com.ipc.uda.notificaton.ds.UserContextTransientNotificationListener;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.util.UdaPrincipal;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;

/**
 * @author mordarsd
 *
 */
public class UserContextTransientTest
{

    private UserContextTransientManager mgr; 
    private UserContext userCtx;
    private UserContextTransient userCtxTrans;
    private UserContextTransientNotificationListener notifListener;
    
    @Before
    public void setUp() throws NamingException
    {
        System.setProperty("weblogic.Name","udas");
//        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
//        System.setProperty(Context.PROVIDER_URL, "t3://localhost:7001");
//        InitialContext ctx = new InitialContext(System.getProperties());
//        ctx.createSubcontext("java:");
//        ctx.createSubcontext("java:/comp");
//        ctx.createSubcontext("java:/comp/jmx");
//        ctx.bind("java:/comp/jmx/runtime", null);

        this.notifListener = new UserContextTransientNotificationListener();
        UdaNotifiable.getInstance().registerNotificationListener(this.notifListener);
        
        this.userCtx = new UserContext(new UserID(new UdaPrincipal("uda_user"), "ABCDEF00001"));
        this.mgr = new UserContextTransientManager(userCtx.getSecurityContext());
        
        // Question - why do we need to initialize a TransientManager when it has public static methods??
        // this.mgr.NewUserContextTransient();
        this.userCtxTrans = UserContextTransientManager.NewUserContextTransient();
        // Uglyness for testing purposes only... 
        ((UserContextTransientImpl)this.userCtxTrans).setTransientId("1");
        

        this.userCtxTrans.setCtx(this.userCtx);
        
        DataServicesSubscriptionHelper.createSubscriptionTo(
                "ds.UserContextTransient." + userCtxTrans.getTransientId() + 
                ".changed", this.userCtx);
        DataServicesSubscriptionHelper.createSubscriptionTo(
                "ds.UserContextTransient." + userCtxTrans.getTransientId() + 
                ".deleted", this.userCtx);
        
        
        
    }

    @Test
    public void testSave() throws StorageFailureException, InterruptedException
    {
        this.mgr.save(this.userCtxTrans);
        
        // sleep so we can see the "changed" notification
        Thread.sleep(2000);
    }

    
    @Test
    public void testGetById() throws StorageFailureException
    {
        UserContextTransient uct = this.mgr.getById(this.userCtxTrans.getTransientId());
        Assert.assertEquals(this.userCtxTrans, uct);
    }
    
    @Test
    public void testDelete() throws StorageFailureException, InterruptedException
    {
        this.mgr.delete(userCtxTrans);
        
        // sleep so we can see the "changed" notification
        Thread.sleep(2000);
    }
    

    
    @After
    public void tearDown()
    {
        UdaNotifiable.getInstance().unregisterNotificationListener(this.notifListener);
        if (this.userCtx != null)
        {
            this.userCtx.dispose();
        }
    }

}
