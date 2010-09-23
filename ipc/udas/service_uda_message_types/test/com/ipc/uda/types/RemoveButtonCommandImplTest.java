/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ipc.ds.base.db.DatabaseConfiguration;
import com.ipc.ds.base.exception.InvalidContextException;
import com.ipc.ds.base.exception.StorageFailureException;
import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.manager.ButtonManager;
import com.ipc.uda.service.callproc.ButtonSheet;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.util.UdaPrincipal;

/**
 * This class is responsible to test the functionality of RemoveButtonCommandImpl
 * 
 * @author Veena Makam
 * 
 */
public class RemoveButtonCommandImplTest
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
     * Test method for {@link com.ipc.uda.types.RemoveButtonCommandImpl#execute()}.
     * 
     * @throws InvalidContextException
     * @throws StorageFailureException
     * @throws NumberFormatException
     */
    @Test
    public void testExecute() throws NumberFormatException, StorageFailureException,
            InvalidContextException
    {
        final UdaPrincipal udaPrincipal = new UdaPrincipal("root");
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));
        System.out.println("UserName : " + user.getSecurityContext().getUserName() + "Password :"
                + user.getSecurityContext().getPassword());
        
        /*
         * 
         * INISIATIATE UID , Button and ButtonSheet
         */

        final String buttonId = "12056";
        final UID id = new UID(buttonId);
        new ButtonSheet(user);

        /*
         * INSTANTIATE UdaRequest , CommandType AND UpdateButtonCommandImpl
         */

        final UdaRequest request = new UdaRequest();
        final CommandType cmd = new CommandType();
        final RemoveButtonCommandImpl remButtnCmnd = new RemoveButtonCommandImpl();

        /*
         * Instantiate RemoveButtonCommandImpl , CommandType, Request and Executable Object
         */
        remButtnCmnd.setUserContext(user);
        remButtnCmnd.setButtonId(id);
        cmd.setRemoveButton(remButtnCmnd);
        request.setCommand(cmd);

        final Executable exe = request.getExecutable();
        Assert.assertTrue(exe instanceof RemoveButtonCommandImpl);
        System.out.println("Executable initialized");

        /*
         * Execute Execute Method
         */
        try
        {
            exe.execute();

        }
        catch (final Throwable e)
        {
            e.printStackTrace();
        }

        // validate if the button is removed
        final ButtonManager buttonMgr = new ButtonManager(user.getSecurityContext());
        Button dsButton = buttonMgr.getById(Integer.parseInt(buttonId));

        Assert.assertNull(dsButton);

    }

}
