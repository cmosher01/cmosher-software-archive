/* Copyright (c) 2009 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import junit.framework.TestCase;

import org.junit.Test;

import com.ipc.uda.service.execution.Executable;

/**
 * This class tests the functionality of DeleteContactCommandImpl
 * 
 * @author VM0044989 Created on 26th Feb 2010
 * 
 */
public class DeleteContactCommandImplTest extends TestCase
{

    @Test
    public final void testExecute()
    {
        final UdaRequest req = new UdaRequest();
        final CommandType cmd = new CommandType();
        final DeletePersonalContactCommand delContactCmd = new DeletePersonalContactCommandImpl();
        cmd.setDeletePersonalContact(delContactCmd);
        req.setCommand(cmd);
        final Executable exe = req.getExecutable();
        assertTrue(exe instanceof DeletePersonalContactCommandImpl);
    }

}
