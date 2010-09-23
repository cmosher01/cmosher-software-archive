/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ipc.ds.base.db.DatabaseConfiguration;
import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.ds.entity.manager.UserSpeakerChannelManager;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.util.UdaPrincipal;

/**
 * @author Veena Makam
 * 
 */
public class RemoveSpeakerChannelCommandImplTest
{

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        DatabaseConfiguration.SetServerName("localhost");
        DatabaseConfiguration.SetDatabaseName("dunkin");
        System.out.println("Connection Established..");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * Test method for {@link com.ipc.uda.types.RemoveSpeakerChannelCommandImpl#execute()}.
     */
    @Test
    public final void testExecute()
    {
        final UdaPrincipal udaPrincipal = new UdaPrincipal("root");
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));

        /*
         * INSTANTIATE UdaRequest CommandType RemoveSpeakerCommand
         */

        final UdaRequest request = new UdaRequest();
        final CommandType cmd = new CommandType();
        final RemoveSpeakerChannelCommandImpl remvChnlComnd = new RemoveSpeakerChannelCommandImpl();

        System.out.println("RemoveSpeaker Channel Command...");

        /*
         * Setting UserContext in RemoveSpeakerCmmand Setting RemoveSpeakerCommand in CommandType
         * variable Setting CommandType in Request variable
         */
        remvChnlComnd.setUserContext(user);
        final int speakerNumber = 2;
        remvChnlComnd.setSpeakerNumber(speakerNumber);
        cmd.setRemoveSpeakerChannel(remvChnlComnd);
        request.setCommand(cmd);

        final Executable exe = request.getExecutable();
        Assert.assertTrue(exe instanceof RemoveSpeakerChannelCommandImpl);
        try
        {
            exe.execute();
        }
        catch (final ExecutionException e)
        {
            e.printStackTrace();
        }

        // Validate if the removing of the channel ID is successful
        final UserSpeakerChannelManager spkrChnlMgr = new UserSpeakerChannelManager(user
                .getSecurityContext());
        try
        {
            final List<UserSpeakerChannel> usrSpkrChnl = spkrChnlMgr
                    .findBySpeakerNumberEqualTo(speakerNumber);

            Assert.assertTrue(usrSpkrChnl.isEmpty());
            if (usrSpkrChnl == null || usrSpkrChnl.size() == 0)
            {
                System.out.println("Speaker Channel is deleted For The User " + user.getUserName()
                        + " successfully");
            }
        }
        catch (final Throwable e)
        {
            e.printStackTrace();
        }
    }

}
