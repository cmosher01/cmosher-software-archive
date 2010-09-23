/* Copyright (c) 2009 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

/*
import java.security.Principal;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.util.UdaPrincipal;
import com.ipc.uda.types.old.GetPersonalDirectoryListQueryImpl;

public class GetPersonalDirectoryQueryImplTest extends TestCase
{

    @Before
    public void setUp() throws Exception
    {
    }

    @After
    public void tearDown() throws Exception
    {
    }

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
        final UserContext user = new UserContext(udaPrincipal);
        final UdaRequest req = new UdaRequest();
        final QueryType qry = new QueryType();
        final GetPersonalDirectoryListQueryImpl personalDirQry = new GetPersonalDirectoryListQueryImpl();
        personalDirQry.setUserContext(user);
        //qry.setGetPersonalDirectoryFolder(personalDirQry);
        req.setQuery(qry);
        final Executable exe = req.getExecutable();
        assertTrue(exe instanceof GetPersonalDirectoryListQueryImpl);
    }

}
*/