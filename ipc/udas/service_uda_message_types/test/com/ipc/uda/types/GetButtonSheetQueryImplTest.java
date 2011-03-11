/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

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
 * @author Veena Makam
 * 
 */
public class GetButtonSheetQueryImplTest extends TestCase
{

    private static SecurityContext context;

    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        DatabaseConfiguration.SetServerName("localhost");
        DatabaseConfiguration.SetDatabaseName("test");
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
                return "root";
            }
        };
        final UdaPrincipal udaPrincipal = new UdaPrincipal(p);
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));
        ;
        final UdaRequest req = new UdaRequest();
        final QueryType qry = new QueryType();
        final GetButtonSheetQueryImpl buttonSheetQry = new GetButtonSheetQueryImpl();
        GetButtonSheetQueryImplTest.context = new BasicSecurityContext("root", "root");

        buttonSheetQry.setUserContext(user);
        qry.setGetButtonSheet(buttonSheetQry);
        req.setQuery(qry);
        final Executable exe = req.getExecutable();
        Assert.assertTrue(exe instanceof GetButtonSheetQueryImpl);

        final Optional<ExecutionResult> exeRes = exe.execute();
        final QueryResult exeResult = (QueryResult) exeRes.get();
        final ButtonSheetQueryResultType buttonRes = exeResult.getButtonSheet();
        Assert.assertNotNull(buttonRes);

        Assert.assertTrue(buttonRes.getButton().size() > 0);

        System.out.println("Rsult........... " + buttonRes.getButton());

        final List<ButtonType> resList = buttonRes.getButton();

        for (final ButtonType buttonResType2 : resList)
        {
            final StringBuffer sb = new StringBuffer();
            final ButtonType buttonResType = buttonResType2;
            sb.append("\nButtonId - " + buttonResType.getButtonId());
            sb.append("\ngetLabel - " + buttonResType.getButtonLabel());
            sb.append("\ngetIcon - " + buttonResType.getIcon());
            sb.append("\ngetType - " + buttonResType.getButtonType());
            System.out.println(sb);
        }

    }
}