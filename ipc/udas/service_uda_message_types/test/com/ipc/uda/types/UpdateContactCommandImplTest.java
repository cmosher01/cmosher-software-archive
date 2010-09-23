/* Copyright (c) 2009 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.security.Principal;

import junit.framework.TestCase;

import org.junit.Test;

import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.util.UdaPrincipal;

/**
 * This class tests the functionality of UpdateContactCommandImpl
 * 
 * @author VM0044989 Created on 26th Feb 2010
 * 
 */
public class UpdateContactCommandImplTest extends TestCase
{

    @Test
    public final void testExecute() throws ExecutionException
    {
        final Principal p = new Principal()
        {
            @Override
            public String getName()
            {
                return "test";
            }
        };
        final UdaPrincipal udaPrincipal = new UdaPrincipal(p);
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));;
        final UdaRequest req = new UdaRequest();
        final CommandType cmd = new CommandType();
        final UpdatePersonalContactCommand updContactCmd = new UpdatePersonalContactCommandImpl();
        cmd.setUpdatePersonalContact(updContactCmd);
        req.setCommand(cmd);
        final Executable exe = req.getExecutable();
        assertTrue(exe instanceof UpdatePersonalContactCommandImpl);

    }

}
