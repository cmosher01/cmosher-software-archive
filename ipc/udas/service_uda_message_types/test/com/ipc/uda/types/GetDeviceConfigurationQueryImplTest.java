/* Copyright (c) 2009 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.security.Principal;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ipc.ds.base.db.DatabaseConfiguration;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.UdaPrincipal;

/**
 * @author Bhavya Bhat
 * 
 */
public class GetDeviceConfigurationQueryImplTest extends TestCase {

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		// DatabaseConfiguration.SetServerName("10.19.123.73");
		// DatabaseConfiguration.SetDatabaseName("dunkin");
		DatabaseConfiguration.SetServerName("10.19.123.76");
		DatabaseConfiguration.SetDatabaseName("dunkin");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
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
				// return "root";
			}
		};
		final UdaPrincipal udaPrincipal = new UdaPrincipal(p);
		final UserContext user = new UserContext(new UserID(udaPrincipal,
				"1013"));
		// "ABCDEF0003"));

		final UdaRequest req = new UdaRequest();
		final QueryType qry = new QueryType();
		final GetDeviceConfigurationQueryImpl devConfQry = new GetDeviceConfigurationQueryImpl();
		devConfQry.setUserContext(user);

		qry.setGetDeviceConfiguration(devConfQry);
		req.setQuery(qry);
		final Executable exe = req.getExecutable();

		Assert.assertTrue(exe instanceof GetDeviceConfigurationQueryImpl);

		Optional<ExecutionResult> queryRes = exe.execute();
		final QueryResult devRes = (QueryResult) queryRes.get();
		DeviceConfigurationResultType res = devRes
				.getDeviceConfigurationResult();

		DeviceType devtype = res.getDevice();

		System.out.println("devType.getId()" + devtype.getId());
	}

}
