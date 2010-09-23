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
import com.ipc.ds.entity.dto.ResourceAOR;
import com.ipc.ds.entity.manager.ButtonManager;
import com.ipc.ds.entity.manager.ResourceAORManager;
import com.ipc.uda.service.callproc.ButtonSheet;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.util.UdaPrincipal;

/**
 * This class is responsible to test the functionality of UpdateButtonCommandImpl
 * 
 * @author Veena Makam
 * 
 */
public class UpdateButtonCommandImplTest
{

    @Before
    public void setUp() throws Exception
    {
        DatabaseConfiguration.SetServerName("localhost");
        DatabaseConfiguration.SetDatabaseName("test");
        System.out.println("Connection Setup.");
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testExecute() throws InvalidContextException, StorageFailureException
    {
        final UdaPrincipal udaPrincipal = new UdaPrincipal("root");
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));
        System.out.println("UserName : " + user.getSecurityContext().getUserName() + "Password :"
                + user.getSecurityContext().getPassword());

        /*
         * 
         * INISIATIATE UID , Button To get Old Button Value
         */

        final String buttonIdStr = "12001";
        final String buttonLabel = "Ext 3001";

        final UID buttonId = new UID(buttonIdStr);
        new ButtonSheet(user);
        

        /*
         * INSTANTIATE UdaRequest , CommandType AND UpdateButtonCommandImpl
         */

        final UdaRequest request = new UdaRequest();
        final CommandType cmd = new CommandType();
        final UpdateButtonCommandImpl updtButtnCmnd = new UpdateButtonCommandImpl();

        /*
         * Setting Values in UpdateButtonCommand
         */

        updtButtnCmnd.setButtonId(buttonId);
        updtButtnCmnd.setUserContext(user);
        updtButtnCmnd.setButtonLabel(buttonLabel);
        updtButtnCmnd.setButtonNumber(1);
        updtButtnCmnd.setIcon(IconTypeEnum.NONE);
        updtButtnCmnd.setIncomingActionCLI(IncomingActionCLITypeEnum.NO_CLI);
        updtButtnCmnd.setIncomingActionPriority(IncomingActionPriorityTypeEnum.LOW);
        updtButtnCmnd.setIncomingActionRings(IncomingActionRingsTypeEnum.REPEAT);
        final String keySeq = "123";
        updtButtnCmnd.setKeySequence(keySeq);
        updtButtnCmnd.setAppearance(1);
        final String resAOR = "3202";
        updtButtnCmnd.setResourceAor(resAOR);

        /*
         * Set the Value of CommandType Variable, And UdaRequest Variable
         */

        cmd.setUpdateButton(updtButtnCmnd);
        request.setCommand(cmd);

        /*
         * Instantiating AND Executing Execute Method
         */

        final Executable exe = request.getExecutable();
        Assert.assertTrue(exe instanceof UpdateButtonCommandImpl);
        try
        {
            exe.execute();
        }
        catch (final ExecutionException e)
        {

            e.printStackTrace();
        }

        /*
         * INISIATIATE UID , Button , ReasourceAOR
         */

        final ButtonManager buttonMgr = new ButtonManager(user.getSecurityContext());
        final Button dsButton = buttonMgr.getById(Integer.parseInt(buttonIdStr));
        final ResourceAORManager resAORMgr = new ResourceAORManager(user.getSecurityContext());
        final ResourceAOR resAORObj = resAORMgr.findByResourceAOREqualing(updtButtnCmnd
                .getResourceAor());

        /*
         * AssertEquals To see Whether Update is Reflected in DATABASE or NOT.
         */

        Assert.assertEquals("Ext 3001", dsButton.getButtonLabel());
        Assert.assertEquals(1, dsButton.getButtonNumber());
        Assert.assertEquals(IconTypeEnum.NONE.value(), dsButton.getIcon().toString());
        Assert.assertEquals(IncomingActionCLITypeEnum.NO_CLI.value(), dsButton
                .getIncomingActionCLI().toString());
        Assert.assertEquals(IncomingActionPriorityTypeEnum.LOW.value(), dsButton
                .getIncomingActionPriority().toString());
        Assert.assertEquals(IncomingActionRingsTypeEnum.REPEAT.value(), dsButton
                .getIncomingActionRings().toString());
        Assert.assertEquals(keySeq, dsButton.getKeySequence());
        Assert.assertEquals(resAOR, resAORObj.getResourceAOR());
    }

}
