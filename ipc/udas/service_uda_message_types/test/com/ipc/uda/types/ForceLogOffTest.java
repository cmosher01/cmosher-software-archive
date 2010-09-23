/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;


import static org.junit.Assert.assertTrue;

import java.security.Principal;

import org.junit.Test;

import com.ipc.uda.service.execution.Executable;


public class ForceLogOffTest {
	
	@Test
	public void nominal() {
	    // FIXME: LogOnCommand was removed in favor or LogOnQuery
		
		final Principal p = new Principal()
        {
            @Override
            public String getName()
            {
                return "root";
            }
        };
        final UdaRequest req = new UdaRequest();
		
		final CommandType cmd = new CommandType();

		final ForceLogOffCommand fLogOff  = new ForceLogOffCommandImpl();
		
		cmd.setForceLogOff(fLogOff);
		
		req.setCommand(cmd);
		
		final Executable exe = req.getExecutable();
		
		assertTrue(exe instanceof ForceLogOffCommand);
	}	
}