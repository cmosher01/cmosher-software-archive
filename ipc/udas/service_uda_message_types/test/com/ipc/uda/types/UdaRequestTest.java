/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ipc.uda.service.execution.Executable;

public class UdaRequestTest
{
    @Test
    public void nominal()
    {
        final UdaRequest req = new UdaRequest();
        final CommandType cmd = new CommandType();
        final AddButtonCommandImpl addButton = new AddButtonCommandImpl();
        cmd.setAddButton(addButton);
        req.setCommand(cmd);
        final Executable exe = req.getExecutable();
        assertTrue(exe instanceof AddButtonCommandImpl);
    }

    @Test
    public void nominal2()
    {
        final UdaRequest req = new UdaRequest();
        final CommandType cmd = new CommandType();
        final ReleaseCallCommandImpl rel = new ReleaseCallCommandImpl();
        cmd.setReleaseCall(rel);
        req.setCommand(cmd);
        final Executable exe = req.getExecutable();
        assertTrue(exe instanceof ReleaseCallCommandImpl);
    }

    @Test(expected=IllegalStateException.class)
    public void error()
    {
        final UdaRequest req = new UdaRequest();
        req.getExecutable();
    }
}
