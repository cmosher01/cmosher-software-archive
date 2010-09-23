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
 * @author Bhavya Bhat
 * 
 */
public class GetSoftphoneConfigurationQueryImplTest extends TestCase
{
    @Override
    @Before
    public void setUp() throws Exception
    {
        DatabaseConfiguration.SetServerName("localhost");
        DatabaseConfiguration.SetDatabaseName("test");
        /*
         * DatabaseConfiguration.SetServerName("10.19.8.224");
         * DatabaseConfiguration.SetDatabaseName("dunkin");
         * DatabaseConfiguration.SetUserName("dunkin");
         * DatabaseConfiguration.SetPassword("dunkin123");
         */

    }

    @Override
    @After
    public void tearDown() throws Exception
    {

    }

    @Test
    public void testExecute() throws ExecutionException
    {
        final Principal principal = new Principal()
        {

            @Override
            public String getName()
            {
                // return "dunkin";
                return "test";
            }
        };
        final UdaPrincipal udaPrincipal = new UdaPrincipal(principal);
        final UdaRequest request = new UdaRequest();
        final QueryType query = new QueryType();
        final UserContext userContxt = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));
        ;

        final UdaRequest req = new UdaRequest();

        final GetSoftphoneConfigurationQueryImpl softphoneConfQuery = new GetSoftphoneConfigurationQueryImpl();
        softphoneConfQuery.setUserContext(userContxt);
        query.setGetSoftphoneConfiguration(softphoneConfQuery);

        req.setQuery(query);
        final Executable exe = req.getExecutable();

        Assert.assertTrue(exe instanceof GetSoftphoneConfigurationQueryImpl);
        final Optional<ExecutionResult> exeRes = exe.execute();
        final QueryResult queryRes = (QueryResult) exeRes.get();
        final SoftphoneConfigurationResultType softphoneRes = queryRes
                .getSoftphoneConfigurationResult();

        // System.out.println( softphoneRes.getAudioAGC());
        // System.out.println(softphoneRes.getAudioCodecs());

    }

}
