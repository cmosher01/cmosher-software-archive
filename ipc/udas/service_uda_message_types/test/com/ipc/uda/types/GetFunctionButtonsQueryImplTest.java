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
 * @author bhavya bhat
 *
 *This class is responsible to test diagnostics
 */

public class GetFunctionButtonsQueryImplTest extends TestCase{
	 private static SecurityContext context;

	    /**
	     * @throws java.lang.Exception
	     */
	    @Override
	    @Before
	    public void setUp() throws Exception
	    {
	        /*DatabaseConfiguration.SetServerName("10.19.123.76");
	        DatabaseConfiguration.SetDatabaseName("dunkin");*/
	    	DatabaseConfiguration.SetServerName("localhost");
	        DatabaseConfiguration.SetDatabaseName("dunkin");
	    }

	    /**
	     * @throws java.lang.Exception
	     */
	    @Override
	    @After
	    public void tearDown() throws Exception
	    {
	    }

	    /**
	     * Test method for {@link com.ipc.uda.types.GetButtonSheetForThePageQueryImpl#execute()}.
	     * 
	     * @throws ExecutionException
	     */
	    @Test
	    public void testExecute() throws ExecutionException
	    {

	        final Principal p = new Principal()
	        {
	            @Override
	            public String getName()
	            {
	                return "roy";
	            }
	        };
	        final UdaPrincipal udaPrincipal = new UdaPrincipal(p);
	        final UserContext user = new UserContext(new UserID(udaPrincipal, "1013"));
	        
	        // This will be called during Log on. Need this to get the list of buttons associated with the user
	       
	        final UdaRequest req = new UdaRequest();
	        final QueryType qry = new QueryType();
	        final GetFunctionButtonsQueryImpl functionButtonQuery = new GetFunctionButtonsQueryImpl();
	        this.context = new BasicSecurityContext("dunkin", "dunkin123");

	        functionButtonQuery.setUserContext(user);
	        qry.setGetFunctionButtons(functionButtonQuery);
	        req.setQuery(qry);
	        final Executable exe = req.getExecutable();
	        Assert.assertTrue(exe instanceof GetFunctionButtonsQueryImpl);

	        final Optional<ExecutionResult> exeRes = exe.execute();
	        final QueryResult exeResult = (QueryResult) exeRes.get();
	        
	        final FunctionButtonsResultType FuncbuttonRes = exeResult.getFunctionButtonsResult();
	        Assert.assertNotNull(FuncbuttonRes);
	     

	        final List<FunctionButtonType> resList = FuncbuttonRes.getFunctionButton();
	        for (final FunctionButtonType buttonResType : resList)
	        {

	        	System.out.println("buttonResType.getButtonId()=="+buttonResType.getButtonId());
	        	System.out.println("buttonResType.getCanonicalName()=="+buttonResType.getCanonicalName());
	        	System.out.println("buttonResType.getType()=="+buttonResType.getType());
	        }

	    }
}
