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
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.UdaPrincipal;

/**
 * @author Veena Makam
 * 
 */
public class GetContactHistoryQueryImplTest extends TestCase
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
     * Test method for {@link com.ipc.uda.types.GetContactHistoryQueryImpl#execute()}.
     */
    @Test
    public void testInstanceContactExecute()
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
        final UserContext user = new UserContext(new UserID(udaPrincipal, "00E0A7050D8A"));
        ;

        final UdaRequest req = new UdaRequest();
        final QueryType qry = new QueryType();
        final GetContactHistoryQueryImpl ctHistQry = new GetContactHistoryQueryImpl();
        ctHistQry.setUserContext(user);
        final UID uid = new UID("" + 71);
        ctHistQry.setContactId(uid);
        ctHistQry.setContactType(DirectoryContactType.INSTANCE);

        qry.setGetContactHistory(ctHistQry);
        req.setQuery(qry);
        final Executable exe = req.getExecutable();
        Assert.assertTrue(exe instanceof GetContactHistoryQueryImpl);

        Optional<ExecutionResult> exeRes;
        ContactHistoryResultType ctHistRes = null;
        try
        {
            exeRes = exe.execute();
            Assert.assertNotNull(exeRes);
            final QueryResult exeResult = (QueryResult) exeRes.get();
            Assert.assertNotNull(exeResult);
            ctHistRes = exeResult.getContactHistoryResult();
            if (ctHistRes != null)
            {
                System.out.println("Result not NULL");
                System.out.println("Result size ------------- "
                        + ctHistRes.getHistoryRecords().size());
            }
            else
            {
                System.out.println("RESULT NULL");
            }
        }
        catch (final ExecutionException lException)
        {
            lException.printStackTrace();
        }

    }

    /**
     * Test method for {@link com.ipc.uda.types.GetContactHistoryQueryImpl#execute()}.
     */
    @Test
    public void testPersonalContactExecute()
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
        final UserContext user = new UserContext(new UserID(udaPrincipal, "00E0A7050D8A"));

        final UdaRequest req = new UdaRequest();
        final QueryType qry = new QueryType();
        final GetContactHistoryQueryImpl ctHistQry = new GetContactHistoryQueryImpl();
        ctHistQry.setUserContext(user);
        final UID uid = new UID("" + 78);
        ctHistQry.setContactId(uid);
        ctHistQry.setContactType(DirectoryContactType.PERSONAL);

        qry.setGetContactHistory(ctHistQry);
        req.setQuery(qry);
        final Executable exe = req.getExecutable();
        Assert.assertTrue(exe instanceof GetContactHistoryQueryImpl);

        Optional<ExecutionResult> exeRes;
        ContactHistoryResultType ctHistRes = null;
        try
        {
            exeRes = exe.execute();
            Assert.assertNotNull(exeRes);
            final QueryResult exeResult = (QueryResult) exeRes.get();
            Assert.assertNotNull(exeResult);
            ctHistRes = exeResult.getContactHistoryResult();
            if (ctHistRes != null)
            {
                System.out.println("Result not NULL");
                System.out.println("Result size ------------- "
                        + ctHistRes.getHistoryRecords().size());
            }
            else
            {
                System.out.println("RESULT NULL");
            }
        }
        catch (final ExecutionException lException)
        {
            lException.printStackTrace();
        }

    }
}
