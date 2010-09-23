/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.security.Principal;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ipc.ds.base.db.DatabaseConfiguration;
import com.ipc.ds.base.security.BasicSecurityContext;
import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.manager.DeviceUDAManager;
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
public class GetCoreConfigurationQueryImplTest extends TestCase
{
    @Override
    @Before
    public void setUp() throws Exception
    {
//        DatabaseConfiguration.SetServerName("10.19.123.73");
//        DatabaseConfiguration.SetDatabaseName("dunkin");

    	DatabaseConfiguration.SetServerName("localhost");
        DatabaseConfiguration.SetDatabaseName("root");

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
                return "root";
                //return "roy";
            }
        };
        final UdaPrincipal udaPrincipal = new UdaPrincipal(principal);
        final UdaRequest request = new UdaRequest();
        final QueryType query = new QueryType();
        final UserContext userContxt = new UserContext(new UserID(udaPrincipal, "1006"));

        final UdaRequest req = new UdaRequest();

        final GetCoreConfigurationQueryImpl coreConfQuery = new GetCoreConfigurationQueryImpl();
        coreConfQuery.setUserContext(userContxt);
        query.setGetCoreConfiguration(coreConfQuery);

        req.setQuery(query);
        final Executable exe = req.getExecutable();

        Assert.assertTrue(exe instanceof GetCoreConfigurationQueryImpl);
        final SecurityContext secCtx = new BasicSecurityContext("dunkin", "dunkin123");
        final DeviceUDAManager manager = new DeviceUDAManager(secCtx);
        final Optional<ExecutionResult> exeRes = exe.execute();

        final QueryResult exeResult = (QueryResult) exeRes.get();
        final CoreConfigurationResultType coreCongResult = exeResult.getCoreConfigurationResult();
        System.out.println(coreCongResult.getLogFileName());
        System.out.println(coreCongResult.getLogFileRotation());
        System.out.println(coreCongResult.getLogLevel());

    }

}
