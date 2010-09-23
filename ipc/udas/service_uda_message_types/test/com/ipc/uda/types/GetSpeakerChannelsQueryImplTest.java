/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
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
public class GetSpeakerChannelsQueryImplTest
{
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        DatabaseConfiguration.SetServerName("localhost");
        DatabaseConfiguration.SetDatabaseName("test");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * Test method for {@link com.ipc.uda.types.GetSpeakerChannelsQueryImpl#execute()}.
     * 
     * @throws ExecutionException
     */
    @Test
    public void testExecute() throws ExecutionException
    {
        final UdaPrincipal udaPrincipal = new UdaPrincipal("root");
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));
        ;

        System.out.println(user.getSecurityContext().getUserName());
        System.out.println(user.getSecurityContext().getPassword());
        final UdaRequest req = new UdaRequest();
        final QueryType qry = new QueryType();

        final GetSpeakerChannelsQueryImpl sprChnlsQry = new GetSpeakerChannelsQueryImpl();
        sprChnlsQry.setUserContext(user);
        qry.setGetSpeakerChannels(sprChnlsQry);
        req.setQuery(qry);

        final Executable exe = req.getExecutable();
        Assert.assertTrue(exe instanceof GetSpeakerChannelsQueryImpl);

        final Optional<ExecutionResult> exeRes = exe.execute();
        final QueryResult exeResult = (QueryResult) exeRes.get();

        final SpeakerChannelResultType udaResult = exeResult.getSpeakerChannelResult();
        Assert.assertTrue(!udaResult.getSpeakerChannel().isEmpty());
        System.out.println(udaResult.getSpeakerChannel().size());
        final List<SpeakerChannelType> udaSprTypeLst = udaResult.getSpeakerChannel();
        for (final SpeakerChannelType sprChl : udaSprTypeLst)
        {
            System.out.println("getCanonicalName -> " + sprChl.getName());
            System.out.println("getResourceAor -> " + sprChl.getResourceAor());
            System.out.println("getChannelNumber ->" + String.valueOf(sprChl.getSpeakerNumber()));
            System.out.println("isInGroup1() -> " + String.valueOf(sprChl.isInGroup1()));
            System.out.println("isInGroup2() -> " + String.valueOf(sprChl.isInGroup2()));
        }
    }

}
