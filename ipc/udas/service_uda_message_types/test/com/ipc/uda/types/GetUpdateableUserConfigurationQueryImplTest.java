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
 * @author Bhavya Bhat
 * 
 */
public class GetUpdateableUserConfigurationQueryImplTest extends TestCase
{
    @Override
    @Before
    public void setUp() throws Exception
    {
        DatabaseConfiguration.SetServerName("localhost");
        DatabaseConfiguration.SetDatabaseName("test");

    }

    @Override
    @After
    public void tearDown() throws Exception
    {

    }

    @Test
    public void testExecute()
    {
        final Principal principal = new Principal()
        {

            @Override
            public String getName()
            {
                return "test";
            }
        };
        final UdaPrincipal udaPrincipal = new UdaPrincipal(principal);
        final UdaRequest request = new UdaRequest();
        final QueryType query = new QueryType();
        final UserContext userContxt = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));
        ;

        final UdaRequest req = new UdaRequest();

        final GetUpdateableUserConfigurationQueryImpl updateUserConfQuery = new GetUpdateableUserConfigurationQueryImpl();
        updateUserConfQuery.setUserContext(userContxt);
        query.setGetUpdateableUserConfiguration(updateUserConfQuery);

        req.setQuery(query);
        final Executable exe = req.getExecutable();

        Assert.assertTrue(exe instanceof GetUpdateableUserConfigurationQueryImpl);

    }

}
