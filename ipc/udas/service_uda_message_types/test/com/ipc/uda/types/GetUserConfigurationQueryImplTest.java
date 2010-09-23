/* Copyright (c) 2009 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.security.Principal;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ipc.ds.base.db.DatabaseConfiguration;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.UdaPrincipal;

/**
 * @author Veena Makam
 * 
 */
public class GetUserConfigurationQueryImplTest extends TestCase
{

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
       /* DatabaseConfiguration.SetServerName("10.19.8.138");
        DatabaseConfiguration.SetDatabaseName("test");
        DatabaseConfiguration.SetUserName("root");
        DatabaseConfiguration.SetPassword("root");*/
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * Test method for {@link com.ipc.uda.types.GetInstanceContactDetailsQueryImpl#execute()}.
     * 
     * @throws ExecutionException
     */
    @Test
    public void testExecute() throws ExecutionException
    {
        final Principal p = new Principal()
        {
            @Override
            public String getName()
            {
                return "root";
            }
        };
        final UdaPrincipal udaPrincipal = new UdaPrincipal(p);
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));;

        final UdaRequest req = new UdaRequest();
        final QueryType qry = new QueryType();
        final GetUserConfigurationQueryImpl userConfQry = new GetUserConfigurationQueryImpl();
        userConfQry.setUserContext(user);
        req.setQuery(qry);
        final Executable exe = req.getExecutable();
        assertTrue(exe instanceof GetUserConfigurationQueryImpl);
        
        //System.out.println(perCtDetailRes.getContact().getFirstName());
    }

}
