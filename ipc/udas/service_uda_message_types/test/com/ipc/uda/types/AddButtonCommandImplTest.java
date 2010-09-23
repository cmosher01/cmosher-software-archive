/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ipc.ds.base.db.DatabaseConfiguration;
import com.ipc.uda.service.callproc.ButtonSheet;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.util.UdaPrincipal;

/**
 * @author Veena Makam
 * 
 */
public class AddButtonCommandImplTest
{

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        DatabaseConfiguration.SetServerName("localhost");
        DatabaseConfiguration.SetDatabaseName("test");
        System.out.println("Conection Setup.");
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * Test method for {@link com.ipc.uda.types.AddButtonCommandImpl#execute()}.
     */
    @Test
    public final void testExecute()
    {
        final UdaPrincipal udaPrincipal = new UdaPrincipal("root");
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));
        System.out.println("User Created.");

        /*
         * Instantiate UdaRequest, CommandType, AddButtonCommandImpl, and ButtonSheet Object
         */
        new ButtonSheet(user);
        final UdaRequest request = new UdaRequest();
        final CommandType cmd = new CommandType();
        final AddButtonCommandImpl addButtnCmd = new AddButtonCommandImpl();

        /*
         * Setting the values for the New Button
         */

        addButtnCmd.setUserContext(user);
        addButtnCmd.setAppearance(1);
        addButtnCmd.setAutoSignal(true);
        addButtnCmd.setButtonLabel("Ext 5405");
        addButtnCmd.setButtonNumber(56);
        addButtnCmd.setButtonType(EnumButtonType.RESOURCE);
        addButtnCmd.setDestination("Speaker");
        addButtnCmd.setDirectoryContactType(DirectoryContactType.PERSONAL);
        addButtnCmd.setIcon(IconTypeEnum.NONE);
        addButtnCmd.setIncomingActionCLI(IncomingActionCLITypeEnum.CLI);
        addButtnCmd.setIncomingActionPriority(IncomingActionPriorityTypeEnum.LOW);
        addButtnCmd.setIncomingActionRings(IncomingActionRingsTypeEnum.REPEAT);
        addButtnCmd.setIncludeInCallHistory(true);
        addButtnCmd.setKeySequence("123");
        addButtnCmd.setPointOfContactId(new UID("1"));
        addButtnCmd.setResourceAor("9959");

        /*
         * Setting the CommandType and Request Object
         */
        cmd.setAddButton(addButtnCmd);
        request.setCommand(cmd);

        /*
         * Instantiate the Executable Object and Executing the execute method.
         */
        final Executable exe = request.getExecutable();
        // Assert to find the exe is the Object of AddButtonCommandImp or not.
        Assert.assertTrue(exe instanceof AddButtonCommandImpl);

        try
        {
            exe.execute();
        }
        catch (final ExecutionException e)
        {
            e.printStackTrace();
        }
    }
}
