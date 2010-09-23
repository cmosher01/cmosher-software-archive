package com.ipc.uda.types;

import java.security.Principal;
import java.util.List;

import junit.framework.Assert;
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
 * 
 * @author Bhavya Bhat
 * 
 *         This class will test the MFU POC impl class
 * 
 */

public class GetMFUPointOfContactsQueryImplTest extends TestCase {
	private static SecurityContext context;

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		// DatabaseConfiguration.SetServerName("10.19.123.76");
		// DatabaseConfiguration.SetDatabaseName("dunkin");
		DatabaseConfiguration.SetServerName("localhost");
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
	 * {@link com.ipc.uda.types.GetButtonSheetForThePageQueryImpl#execute()}.
	 * 
	 * @throws ExecutionException
	 */
	@Test
	public void testExecute() throws ExecutionException {

		final Principal p = new Principal() {
			@Override
			public String getName() {
				// return "roy";
				return "root";
			}
		};
		final UdaPrincipal udaPrincipal = new UdaPrincipal(p);
		final UserContext user = new UserContext(new UserID(udaPrincipal,
				"ABCDEF0001"));

		final UdaRequest req = new UdaRequest();
		final QueryType qry = new QueryType();
		final GetMFUPointOfContactsQueryImpl mfuPOCQuery = new GetMFUPointOfContactsQueryImpl();
		this.context = new BasicSecurityContext("dunkin", "dunkin123");

		mfuPOCQuery.setUserContext(user);
		qry.setGetMFUPointOfContacts(mfuPOCQuery);
		req.setQuery(qry);
		final Executable exe = req.getExecutable();
		Assert.assertTrue(exe instanceof GetMFUPointOfContactsQueryImpl);

		final Optional<ExecutionResult> exeRes = exe.execute();
		final QueryResult exeResult = (QueryResult) exeRes.get();

		final MfuPointOfContactResultType mfuRes = exeResult
				.getMfuPointOfContactResult();
		Assert.assertNotNull(mfuRes);

		final List<MfuPointOfContactType> resList = mfuRes.getMfuContact();
		for (final MfuPointOfContactType ResType : resList) {

			System.out
					.println(" ResType.getCompany()==" + ResType.getCompany());
			System.out.println(" ResType.getCanonicalName()=="
					+ ResType.getCanonicalName());
			System.out.println(" ResType.getData()==" + ResType.getData());
		}

	}
}
