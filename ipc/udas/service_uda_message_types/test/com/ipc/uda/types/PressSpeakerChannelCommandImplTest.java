/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ipc.ds.base.db.DatabaseConfiguration;
import com.ipc.ds.base.security.SecurityContext;
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
public class PressSpeakerChannelCommandImplTest
{

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        DatabaseConfiguration.SetServerName("localhost");
        DatabaseConfiguration.SetDatabaseName("dunkin");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * Test method for {@link com.ipc.uda.types.PressSpeakerChannelCommandImpl#execute()}.
     * 
     * @throws ExecutionException
     */
    @Test
    public final void testExecute()
    {
        final UdaPrincipal udaPrincipal = new UdaPrincipal("root");
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));

        System.out.println(user.getSecurityContext().getUserName());
        System.out.println(user.getSecurityContext().getPassword());
        final UdaRequest req = new UdaRequest();
        final CommandType cmd = new CommandType();
        final PressSpeakerChannelCommandImpl actSprCmd = new PressSpeakerChannelCommandImpl();
        actSprCmd.setUserContext(user);
        final int speakerNumber = 4;
        actSprCmd.setSpeakerNumber(speakerNumber);
        cmd.setPressSpeakerChannel(actSprCmd);
        req.setCommand(cmd);

        final Executable exe = req.getExecutable();
        Assert.assertTrue(actSprCmd instanceof PressSpeakerChannelCommandImpl);
        try
        {
            final SecurityContext basicSecCtx = user.getSecurityContext();
            final UserSpeakerChannelManager usrChlMgr = new UserSpeakerChannelManager(basicSecCtx);

            final List<UserSpeakerChannel> dsUsrChlLst = usrChlMgr
                    .findBySpeakerNumberEqualTo(speakerNumber);
            if (dsUsrChlLst != null && !dsUsrChlLst.isEmpty())
            {
                final UserSpeakerChannel dsUsrChl = dsUsrChlLst.get(0);
                if (dsUsrChl == null)
                {
                    System.out.println("No speaker channel associated with the user in DB");
                }
                System.out.println("Status Value Before PressSpeakerChannelCommand is executed: "
                        + dsUsrChl.getActiveStatus());
                final boolean statusBefore = dsUsrChl.getActiveStatus();

                exe.execute();

                final boolean statusAfter = dsUsrChl.getActiveStatus();
                System.out.println("Status Value After PressSpeakerChannelCommand is executed: "
                        + dsUsrChl.getActiveStatus());
                Assert.assertNotSame(statusBefore, statusAfter);
            }
            else
            {
                System.out.println("No speaker channels associated with the user in DB");
            }
        }
        catch (final Throwable lException)
        {
            lException.printStackTrace();
        }
    }
}
