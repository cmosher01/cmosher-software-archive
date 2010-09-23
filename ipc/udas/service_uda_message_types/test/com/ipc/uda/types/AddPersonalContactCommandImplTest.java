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
import com.ipc.uda.service.util.UdaPrincipal;

/**
 * This class tests the functionality of AddContactCommandImpl
 * 
 * @author VM0044989 Created on 26th Feb 2010
 * 
 */
public class AddPersonalContactCommandImplTest extends TestCase
{

    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception
    {
        DatabaseConfiguration.SetServerName("10.19.123.75");
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
     * Test method for {@link com.ipc.uda.types.AddPersonalContactCommandImpl#execute()}.
     * 
     * @throws ExecutionException
     */
    @Test
    public final void testExecuteAddInstanceToPersonal() throws ExecutionException
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
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));
        ;

        final UdaRequest req = new UdaRequest();
        final CommandType cmd = new CommandType();
        final AddPersonalContactCommandImpl addContactCmd = new AddPersonalContactCommandImpl();
        final ContactType ct = new ContactType();
        final int ctId = 25;

        ct.setAor("Veena@techmahindra.com");
        ct.setBusinessGroup("personal");
        ct.setCity("Bangalore");
        ct.setCompanyName("TechM");
        ct.setContactCategories("TechM");
        ct.setContactId(UID.fromDataServicesID(ctId));
        ct.setContactType(ContactCategoryType.PERSONAL);
        ct.setFirstName("Roy");
        ct.setLastName("Ding");
        ct.setUserAor("roy");

        addContactCmd.setContact(ct);
        addContactCmd.setUserContext(user);
        cmd.setAddPersonalContact(addContactCmd);
        req.setCommand(cmd);
        final Executable exe = req.getExecutable();
        Assert.assertTrue(exe instanceof AddPersonalContactCommandImpl);

        exe.execute();
    }

    /**
     * Test method for {@link com.ipc.uda.types.AddPersonalContactCommandImpl#execute()}.
     * 
     * @throws ExecutionException
     */
    @Test
    public final void testExecuteAddToPersonal() throws ExecutionException
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
        final UserContext user = new UserContext(new UserID(udaPrincipal, "ABCDEF0001"));
        ;

        final UdaRequest req = new UdaRequest();
        final CommandType cmd = new CommandType();
        final AddPersonalContactCommandImpl addContactCmd = new AddPersonalContactCommandImpl();
        final ContactType ct = new ContactType();
        final String ctId = "398473874-sd347346-kjds37463746";

        ct.setAor("Veena@techmahindra.com");
        ct.setBusinessGroup("personal");
        ct.setCity("Bangalore");
        ct.setCompanyName("TechM");
        ct.setContactCategories("TechM");
        ct.setContactId(new UID(ctId));
        ct.setContactType(ContactCategoryType.PERSONAL);
        ct.setFirstName("Roy");
        ct.setLastName("Ding");
        ct.setUserAor("roy");

        ContactType.PointsOfContact pocsType = ct.getPointsOfContact();
        pocsType = new ContactType.PointsOfContact();
        final PointOfContactType pocType = new PointOfContactType();
        pocType.setContactId(25);
        pocType.setData("roy");
        pocType.setDefault(true);
        pocType.setDescriptor("ICM RoyDin");
        pocType.setMediaType(EnumMediaType.ICM);
        pocsType.getPointOfContact().add(pocType);
        ct.setPointsOfContact(pocsType);

        addContactCmd.setContact(ct);
        addContactCmd.setUserContext(user);
        cmd.setAddPersonalContact(addContactCmd);
        req.setCommand(cmd);
        final Executable exe = req.getExecutable();
        Assert.assertTrue(exe instanceof AddPersonalContactCommandImpl);

        exe.execute();
    }
}
