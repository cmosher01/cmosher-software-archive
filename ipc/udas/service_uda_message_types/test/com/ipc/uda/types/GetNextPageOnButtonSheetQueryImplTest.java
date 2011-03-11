/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.security.Principal;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ipc.ds.base.db.DatabaseConfiguration;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.util.UdaPrincipal;

/**
 * @author Veena Makam
 * 
 */
public class GetNextPageOnButtonSheetQueryImplTest extends TestCase
{

    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        DatabaseConfiguration.SetServerName("localhost");
        DatabaseConfiguration.SetDatabaseName("test");
    }

    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * Test method for {@link com.ipc.uda.types.GetNextPageOnButtonSheetQueryImpl#execute()}.
     */
    @Test
    public void testExecute()
    {
        final Principal p = new Principal()
        {
            @Override
            public String getName()
            {
                return "test";
            }
        };
        final UdaPrincipal udaPrincipal = new UdaPrincipal(p);
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));
        ;
        final UdaRequest req = new UdaRequest();
        final QueryType qry = new QueryType();
        final GetNextPageOnButtonSheetQueryImpl buttonSheetQry = new GetNextPageOnButtonSheetQueryImpl();
        buttonSheetQry.setUserContext(user);
        qry.setGetNextPageOnButtonSheet(buttonSheetQry);
        req.setQuery(qry);
        final Executable exe = req.getExecutable();
        Assert.assertTrue(exe instanceof GetNextPageOnButtonSheetQueryImpl);
    }

}