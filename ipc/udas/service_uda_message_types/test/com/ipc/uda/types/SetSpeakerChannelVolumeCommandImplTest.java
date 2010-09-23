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
public class SetSpeakerChannelVolumeCommandImplTest
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
     * Test method for {@link com.ipc.uda.types.SetSpeakerChannelVolumeCommandImpl#execute()}.
     * 
     * @throws ExecutionException
     */
    @Test
    public final void testExecute() throws ExecutionException
    {
        final UdaPrincipal udaPrincipal = new UdaPrincipal("root");
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));
        ;

        System.out.println(user.getSecurityContext().getUserName());
        System.out.println(user.getSecurityContext().getPassword());
        final UdaRequest req = new UdaRequest();
        final CommandType cmd = new CommandType();

        final SetSpeakerChannelVolumeCommandImpl sprVolCmd = new SetSpeakerChannelVolumeCommandImpl();
        sprVolCmd.setUserContext(user);
        final int speakerVolume = 3;
        sprVolCmd.setLevel(speakerVolume);
        sprVolCmd.speakerNumber = 4;

        // cmd.setSetSpeakerVolume(sprVolCmd);
        req.setCommand(cmd);

        final Executable exe = req.getExecutable();
        Assert.assertTrue(sprVolCmd instanceof SetSpeakerChannelVolumeCommandImpl);

        exe.execute();

        final SecurityContext basicSecCtx = user.getSecurityContext();
        final UserSpeakerChannelManager usrChlMgr = new UserSpeakerChannelManager(basicSecCtx);
        try
        {
            final List<UserSpeakerChannel> dsUsrChlLst = usrChlMgr
                    .findBySpeakerNumberEqualTo(sprVolCmd.speakerNumber);
            Assert.assertNotNull(dsUsrChlLst);
            Assert.assertTrue(!dsUsrChlLst.isEmpty());

            final UserSpeakerChannel dsUsrChl = dsUsrChlLst.get(0);
            Assert.assertEquals(dsUsrChl.getVolume(), speakerVolume);
            System.out.println("DS USR SPR CHNL ID: " + dsUsrChl.getId());
            System.out.println("Speaker number: " + dsUsrChl.getSpeakerNumber());
            System.out.println("DS UserSpeakerChannel volume for UID " + dsUsrChl.getId()
                    + " is : " + dsUsrChl.getVolume());
        }
        catch (final Throwable lException)
        {
            lException.printStackTrace();
        }
    }

}
