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
import com.ipc.ds.entity.dto.PointOfContact;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.UdaPrincipal;
import com.ipc.uda.types.ContactType.PointsOfContact;

/**
 * @author Veena Makam
 * 
 */
public class GetContactDetailsQueryImplTest extends TestCase
{

    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        DatabaseConfiguration.SetServerName("localhost");
        DatabaseConfiguration.SetDatabaseName("dunkin");
        System.out.println("Connected..");
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
     * Test method for {@link com.ipc.uda.types.GetContactDetailsQueryImpl#execute()}.
     */
    @Test
    public void testExecuteInstance()
    {
        final Principal p = new Principal()
        {
            @Override
            public String getName()
            {
                return "ravi";
            }
        };
        final UdaPrincipal udaPrincipal = new UdaPrincipal(p);
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));

        final UdaRequest req = new UdaRequest();
        final QueryType qry = new QueryType();
        final GetContactDetailsQueryImpl contactDetailsQry = new GetContactDetailsQueryImpl();
        contactDetailsQry.setUserContext(user);
        final UID uid = new UID("" + 1);
        contactDetailsQry.setContactId(uid);
        contactDetailsQry.setContactType(DirectoryContactType.INSTANCE);
        qry.setGetContactDetails(contactDetailsQry);
        req.setQuery(qry);
        final Executable exe = req.getExecutable();
        Assert.assertTrue(exe instanceof GetContactDetailsQueryImpl);

        System.out.println("Testing Instance Contact for ContactId : " + uid.toString());

        Optional<ExecutionResult> exeRes;
        try
        {
            exeRes = exe.execute();
            Assert.assertNotNull(exeRes);
            final QueryResult exeResult = (QueryResult) exeRes.get();
            Assert.assertNotNull(exeResult);
            final ContactDetailsResultType ctDetailRes = exeResult.getContactDetailsResult();
            Assert.assertTrue(ctDetailRes.getContact().getContactId().equals(uid));
            PointsOfContact pocConct = ctDetailRes.getContact().getPointsOfContact();
            List<PointOfContactType> poc = pocConct.getPointOfContact();
            for (int i = 0; i < poc.size(); i++)
            {
                System.out.println("MediaType : " + poc.get(i).getMediaType());
                System.out.println("Contact Data : " + poc.get(i).getData());
                if(poc.get(i).getMediaType().value()== EnumMediaType.ICM.value()) {
                    Assert.assertEquals(poc.get(i).getData(),ctDetailRes.getContact().getUserAor());
                }
                else {
                    Assert.assertNotSame(poc.get(i).getData(), ctDetailRes.getContact().getUserAor());
                }
            }
            System.out.println("UserAor : " + ctDetailRes.getContact().getUserAor());

        }
        catch (final ExecutionException lException)
        {
            lException.printStackTrace();
        }
    }

    @Test
    public void testExecutePersonal()
    {
        final Principal p = new Principal()
        {
            @Override
            public String getName()
            {
                return "derek";
            }
        };
        final UdaPrincipal udaPrincipal = new UdaPrincipal(p);
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));
        ;

        final UdaRequest req = new UdaRequest();
        final QueryType qry = new QueryType();
        final GetContactDetailsQueryImpl contactDetailsQry = new GetContactDetailsQueryImpl();
        contactDetailsQry.setUserContext(user);
        final UID uid = new UID("" + 14);
        contactDetailsQry.setContactId(uid);
        contactDetailsQry.setContactType(DirectoryContactType.PERSONAL);
        qry.setGetContactDetails(contactDetailsQry);
        req.setQuery(qry);
        final Executable exe = req.getExecutable();
        Assert.assertTrue(exe instanceof GetContactDetailsQueryImpl);

        System.out.println("Testing Personal Contact for ContactId : " + uid.toString());
        
        Optional<ExecutionResult> exeRes;
        try
        {
            exeRes = exe.execute();
            Assert.assertNotNull(exeRes);
            final QueryResult exeResult = (QueryResult) exeRes.get();
            Assert.assertNotNull(exeResult);
            final ContactDetailsResultType ctDetailRes = exeResult.getContactDetailsResult();
            Assert.assertTrue(ctDetailRes.getContact().getContactId().equals(uid));
            PointsOfContact pocConct = ctDetailRes.getContact().getPointsOfContact();
            List<PointOfContactType> poc = pocConct.getPointOfContact();
            for(int i=0 ;i<poc.size(); i++) {
                System.out.println("MediaType : " + poc.get(i).getMediaType());
                System.out.println("Contact Data : " + poc.get(i).getData());
                if(poc.get(i).getMediaType().value()== EnumMediaType.ICM.value()) {
                    Assert.assertEquals(poc.get(i).getData(),ctDetailRes.getContact().getUserAor());
                }
                else {
                    Assert.assertNotSame(poc.get(i).getData(), ctDetailRes.getContact().getUserAor());
                }
                System.out.println("UserAor : " + ctDetailRes.getContact().getUserAor());
            }
            
        }
        catch (final ExecutionException lException)
        {
            lException.printStackTrace();
        }
    }
}
