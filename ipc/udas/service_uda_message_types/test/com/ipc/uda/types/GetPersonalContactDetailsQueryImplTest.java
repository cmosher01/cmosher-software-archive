/* Copyright (c) 2009 IPC Systems, Inc. All rights reserved. */

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
public class GetPersonalContactDetailsQueryImplTest extends TestCase
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
     * Test method for {@link com.ipc.uda.types.GetPersonalContactDetailsQueryImpl#execute()}.
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
        final GetPersonalContactDetailsQueryImpl personalContactDetailsQry = new GetPersonalContactDetailsQueryImpl();
        personalContactDetailsQry.setUserContext(user);
        final UID uid = new UID("" + 1);
        personalContactDetailsQry.setContactId(uid);
        qry.setGetPersonalContactDetails(personalContactDetailsQry);
        req.setQuery(qry);
        final Executable exe = req.getExecutable();
        Assert.assertTrue(exe instanceof GetPersonalContactDetailsQueryImpl);

        final Optional<ExecutionResult> exeRes = exe.execute();
        final QueryResult exeResult = (QueryResult) exeRes.get();
        final PersonalContactDetailsResult perCtDetailRes = exeResult.getPersonalContactDetails();

        Assert.assertTrue(perCtDetailRes.getContact().getContactId().equals(uid));

    }

}
