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
public class GetDirectoryNameListQueryImplTest extends TestCase
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
     * Test method for {@link com.ipc.uda.types.GetDirectoryNameListQueryImpl#execute()}.
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
        final GetDirectoryNameListQueryImpl dirNameListQry = new GetDirectoryNameListQueryImpl();
        dirNameListQry.setUserContext(user);

        qry.setGetDirectoryNameList(dirNameListQry);
        req.setQuery(qry);

        final Executable exe = req.getExecutable();
        Assert.assertTrue(exe instanceof GetDirectoryNameListQueryImpl);

        final Optional<ExecutionResult> exeRes = exe.execute();
        final QueryResult exeResult = (QueryResult) exeRes.get();

        final DirectoryNameListResult dirNameListRes = exeResult.getDirectoryNameList();
        Assert.assertTrue(dirNameListRes != null && dirNameListRes.getCategory().size() > 0);
        final List<CategoryType> catList = dirNameListRes.getCategory();

        for (final CategoryType catType : catList)
        {
            Assert.assertNotNull(catType.getDirectoryID().toString());
            System.out.println("Directory ID: " + catType.getDirectoryID().toString());
            Assert.assertNotNull(catType.getName());
            System.out.println("Directory Name: " + catType.getName());
        }
    }

}
