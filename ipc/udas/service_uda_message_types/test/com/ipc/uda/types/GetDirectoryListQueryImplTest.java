/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.security.Principal;
import java.util.List;

import junit.framework.Assert;
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
public class GetDirectoryListQueryImplTest extends TestCase
{

    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
//        DatabaseConfiguration.SetServerName("10.19.123.76");
//        DatabaseConfiguration.SetDatabaseName("dunkin");
        DatabaseConfiguration.SetServerName("localhost");
        DatabaseConfiguration.SetDatabaseName("root");
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
     * Test method for {@link com.ipc.uda.types.GetDirectoryListQueryImpl#execute()}.
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
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));
        ;

        final UdaRequest req = new UdaRequest();
        final QueryType qry = new QueryType();
        final GetDirectoryListQueryImpl dirListQry = new GetDirectoryListQueryImpl();
        dirListQry.setUserContext(user);

        qry.setGetDirectoryList(dirListQry);
        req.setQuery(qry);
        final Executable exe = req.getExecutable();
        Assert.assertTrue(exe instanceof GetDirectoryListQueryImpl);

        final Optional<ExecutionResult> exeRes = exe.execute();
        final QueryResult exeResult = (QueryResult) exeRes.get();

        final DirectoryListResult dirListResult = exeResult.getDirectoryList();
        Assert.assertNotNull(dirListResult);
        DirectoryListResult dirRes = exeResult.getDirectoryList();
        List<ImmutableContactType> list = dirRes.getContact();
        
        for(ImmutableContactType con : list)
        {
        	System.out.println("con.getFirstName()"+ con.getFirstName());
        	System.out.println("con.getUserAOR()"+ con.getUserAOR());
        	System.out.println("con.getLastName()"+ con.getLastName());
        	
        }
        
    }

}
