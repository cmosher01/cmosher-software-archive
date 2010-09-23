/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ipc.ds.base.db.DatabaseConfiguration;
import com.ipc.ds.entity.dto.ResourceAOR;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.ds.entity.manager.ResourceAORManager;
import com.ipc.ds.entity.manager.UserSpeakerChannelManager;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.util.UdaPrincipal;
import com.ipc.uda.types.util.SpeakerUtil;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * This class is responsible to test the functionality of UpdateSpeakerChannelCommandImpl
 * 
 * @author Veena Makam
 * 
 */
public class UpdateSpeakerChannelCommandImplTest
{

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        DatabaseConfiguration.SetServerName("10.19.123.75");
        DatabaseConfiguration.SetDatabaseName("dunkin");
        System.out.println("Connection Setup.");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * Test method for {@link com.ipc.uda.types.UpdateSpeakerChannelCommandImpl#execute()}.
     */
    @Test
    public final void testExecute()
    {
        final UdaPrincipal principal = new UdaPrincipal("roy");
        final UserContext user = new UserContext(new UserID(principal, "ABCDEF0001"));
        System.out.println("User Created.");

        /*
         * Instantiate UpdateSpeakerChannelImpl, CommandType, UdaRequest Object
         */
        final UpdateSpeakerChannelCommandImpl updtSpkrChnlCmd = new UpdateSpeakerChannelCommandImpl();
        final CommandType cmd = new CommandType();
        final UdaRequest request = new UdaRequest();

        /*
         * Setting Values To Update Speaker Channel
         */
        final String name = "Channel No 23";
        final String resAOR = "9999";
        final int app = 2;
        final int spkrNo = 3;

        updtSpkrChnlCmd.setUserContext(user);
        updtSpkrChnlCmd.setAppearance(app);
        updtSpkrChnlCmd.setInGroup1(true);
        updtSpkrChnlCmd.setInGroup2(true);
        updtSpkrChnlCmd.setName(name);
        updtSpkrChnlCmd.setResourceAor(resAOR);
        updtSpkrChnlCmd.setSpeakerNumber(spkrNo);

        /*
         * Setting CommandType , and Request Object
         */
        cmd.setUpdateSpeakerChannel(updtSpkrChnlCmd);
        request.setCommand(cmd);

        /*
         * Initiate Executable Object and Assert to Check the Object Type
         */
        final Executable exe = request.getExecutable();
        Assert.assertTrue(exe instanceof UpdateSpeakerChannelCommandImpl);

        try
        {
            exe.execute();

            final UserSpeakerChannelManager spkrChanlMgr = new UserSpeakerChannelManager(user
                    .getSecurityContext());
            final UserCDI usrCDI = UDAAndDSEntityUtil.getUserCDI(user);
            final List<UserSpeakerChannel> usrSpkrChnlLst = spkrChanlMgr
                    .getUserSpeakerChannelsFor(usrCDI);

            if (usrSpkrChnlLst != null && !usrSpkrChnlLst.isEmpty())
            {
                final UserSpeakerChannel usrSpkrChnl = SpeakerUtil.getUserSpeakerChannelWithInput(
                        usrSpkrChnlLst, spkrNo);

                if (usrSpkrChnl == null)
                {
                    System.out
                            .println("No speaker channel for the given user in db with the speaker number: "
                                    + spkrNo);
                }
                else
                {
                    final ResourceAORManager resAORMgr = new ResourceAORManager(user
                            .getSecurityContext());
                    final ResourceAOR resAor = resAORMgr.getResourceAORFor(usrSpkrChnl);

                    Assert.assertEquals(updtSpkrChnlCmd.isInGroup1(), usrSpkrChnl.getIsInGroup1());
                    Assert.assertEquals(updtSpkrChnlCmd.isInGroup2(), usrSpkrChnl.getIsInGroup2());
                    Assert.assertEquals(name, usrSpkrChnl.getLabel());
                    Assert.assertEquals(resAOR, resAor.getResourceAOR());
                    Assert.assertEquals(app, usrSpkrChnl.getAppearance());
                    Assert.assertEquals(spkrNo, usrSpkrChnl.getSpeakerNumber());
                }
            }
        }
        catch (final Throwable e)
        {
            e.printStackTrace();
        }
    }
}
