/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ipc.ds.base.db.DatabaseConfiguration;
import com.ipc.ds.base.exception.InvalidContextException;
import com.ipc.ds.base.exception.StorageFailureException;
import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.ds.entity.manager.UserSpeakerChannelManager;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.util.UdaPrincipal;

/**
 * This class is responsible to test the functionality of AddSpeakerChannelCommandImpl
 * 
 * @author Veena Makam
 * 
 */
public class AddSpeakerChannelCommandImplTest
{

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        DatabaseConfiguration.SetServerName("localhost");
        DatabaseConfiguration.SetDatabaseName("test");
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
     * Test method for {@link com.ipc.uda.types.AddSpeakerChannelCommandImpl#execute()}.
     */
    @Test
    public final void testExecute()
    {
        /*
         * Setting UdaPrincipal and Create UserContext Object
         */
        final UdaPrincipal udaPrincipal = new UdaPrincipal("root");
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));

        /*
         * Instantiate UdaRequest, CommandType, AddSpeakerChammelCommandImpl Object
         */
        final UdaRequest request = new UdaRequest();
        final CommandType cmd = new CommandType();
        final AddSpeakerChannelCommandImpl addSpkrChnlCmd = new AddSpeakerChannelCommandImpl();

        //
        final UserSpeakerChannelManager usrSpkrChnlMgr = new UserSpeakerChannelManager(user
                .getSecurityContext());
        List<UserSpeakerChannel> oldUsrSpkrChnls;
        int size = 0;
        try
        {
            oldUsrSpkrChnls = usrSpkrChnlMgr.getAll();
            size = oldUsrSpkrChnls.size();
        }
        catch (final InvalidContextException e1)
        {
            e1.printStackTrace();
        }
        catch (final StorageFailureException e1)
        {
            e1.printStackTrace();
        }

        /*
         * Setting Values for the New Speaker
         */
        final int app = 1;
        final String name = "Speaker 20";
        final int spkrNo = 20;
        final String resAOR = "1234";

        addSpkrChnlCmd.setUserContext(user);
        addSpkrChnlCmd.setAppearance(app);
        addSpkrChnlCmd.setInGroup1(true);
        addSpkrChnlCmd.setInGroup2(true);
        addSpkrChnlCmd.setName(name);
        addSpkrChnlCmd.setResourceAor(resAOR);
        addSpkrChnlCmd.setSpeakerNumber(spkrNo);

        /*
         * Setting UdaRequest and CommandType Object
         */
        cmd.setAddSpeakerChannel(addSpkrChnlCmd);
        request.setCommand(cmd);

        /*
         * Instantiate Executable Object and Assert to check that Object is the Object of
         * AddSpeakerChannelCommandImpl
         */
        final Executable exe = request.getExecutable();
        Assert.assertTrue(exe instanceof AddSpeakerChannelCommandImpl);

        /*
         * Executing Executable Object
         */
        try
        {
            exe.execute();
            final List<UserSpeakerChannel> newUsrSpkerChnl = usrSpkrChnlMgr.getAll();
            Assert.assertEquals(size + 1, newUsrSpkerChnl.size());
        }
        catch (final Throwable e)
        {
            e.printStackTrace();
        }

    }

}
