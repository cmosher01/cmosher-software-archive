/* Copyright (c) 2009 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.security.Principal;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ipc.ds.base.db.DatabaseConfiguration;
import com.ipc.ds.base.security.BasicSecurityContext;
import com.ipc.ds.base.security.SecurityContext;

import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.UdaPrincipal;

/**
 * @author bhavya bhat
 * 
 */
public class SendDiagnosticsResultsCommandImplTest extends TestCase {

	/**
	 * @throws java.lang.Exception
	 */
	private static SecurityContext context;

	@Before
	public void setUp() throws Exception {

//		DatabaseConfiguration.SetServerName("10.19.123.226");
//		DatabaseConfiguration.SetDatabaseName("dunkin");

		DatabaseConfiguration.SetServerName("localhost");
		DatabaseConfiguration.SetDatabaseName("dunkin");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.ipc.uda.types.GetInstanceContactDetailsQueryImpl#execute()}.
	 * 
	 * @throws ExecutionException
	 */
	@Test
	public void testExecute() throws ExecutionException {
		final Principal p = new Principal() {
			@Override
			public String getName() {
				return "roy";
			}
		};
		final UdaPrincipal udaPrincipal = new UdaPrincipal(p);
		final UserContext user = new UserContext(new UserID(udaPrincipal,
				"ABCDEF0003"));

		;

		final UdaRequest req = new UdaRequest();
		final CommandType command = new CommandType();
		final SendDiagnosticsResultsCommandImpl diagResCommand = new SendDiagnosticsResultsCommandImpl();
		diagResCommand.setUserContext(user);

		DiagnosticsResultType diagType = new DiagnosticsResultType();
		diagType.setJobID(5);
		diagType.setOutput("outputResValue");
		DiagnosticsResult diagRes = DiagnosticsResult.PARTIAL;
		diagType.setResult(diagRes);
		diagResCommand.getDiagnosticsResult().add(diagType);
		// diagCommnad.getDiagnosticsResult().add(diagType);
		command.setSendDiagnostics(diagResCommand);
		req.setCommand(command);
		final Executable exe = req.getExecutable();
		assertTrue(exe instanceof SendDiagnosticsResultsCommandImpl);

		Optional<ExecutionResult> res = exe.execute();

		// System.out.println(perCtDetailRes.getContact().getFirstName());
	}

}
