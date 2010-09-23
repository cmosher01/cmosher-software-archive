/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.callproc;

import static org.junit.Assert.assertTrue;

import java.security.Principal;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ipc.ds.base.db.DatabaseConfiguration;
import com.ipc.ds.base.security.BasicSecurityContext;
import com.ipc.ds.base.security.SecurityContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.util.UdaPrincipal;

/**
 * @author Veena Makam
 * 
 */
public class ButtonSheetTest
{

    private static UdaPrincipal udaPrincipal;
    private static UserContext user;
    private static SecurityContext context;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        DatabaseConfiguration.SetServerName("localhost");
        DatabaseConfiguration.SetDatabaseName("test");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * Test method for
     * {@link com.ipc.uda.service.callproc.ButtonSheet#populate(com.ipc.ds.base.security.SecurityContext, com.ipc.uda.service.util.UdaPrincipal)}
     * .
     */
    @Test
    public void testPopulate()
    {
        final Principal p = new Principal()
        {
            @Override
            public String getName()
            {
                return "root";
            }
        };
        udaPrincipal = new UdaPrincipal(p);
        user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));
        context = new BasicSecurityContext("root", "root");
        ButtonSheet bSheet = new ButtonSheet(user);
        assertTrue(!bSheet.isEmpty());
    }

    /**
     * Test method for {@link com.ipc.uda.service.callproc.ButtonSheet#getButtonsOfPage(int)}.
     */
    @Test
    public void testGetButtonsOfPage()
    {
        final Principal p = new Principal()
        {
            @Override
            public String getName()
            {
                return "root";
            }
        };
        udaPrincipal = new UdaPrincipal(p);
        user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));
        context = new BasicSecurityContext("root", "root");
        ButtonSheet bSheet = new ButtonSheet(user);
        assertTrue(!bSheet.isEmpty());
        Map<Integer, UdaButton> pageMap = bSheet.getButtonsOfPage(1);
        assertTrue(pageMap.size() > 0);
    }

}
