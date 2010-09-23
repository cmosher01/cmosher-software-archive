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
import com.ipc.ds.base.exception.InvalidContextException;
import com.ipc.ds.base.exception.StorageFailureException;
import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.PersonalPointOfContact;
import com.ipc.ds.entity.internal.manager.base.PersonalPointOfContactBaseManager;
import com.ipc.ds.entity.manager.ButtonManager;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.util.UdaPrincipal;

/**
 * @author Veena Makam
 * 
 */
public class AddPersonalPOCToFavoritesCommandImplTest extends TestCase
{

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
     * Test method for {@link com.ipc.uda.types.AddPersonalPOCToFavoritesCommandImpl#execute()}.
     * 
     * @throws StorageFailureException
     * @throws InvalidContextException
     * @throws ExecutionException
     */
    @Test
    public void testExecute() throws InvalidContextException, StorageFailureException,
            ExecutionException
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

        final UdaRequest req = new UdaRequest();
        final CommandType cmd = new CommandType();
        final AddPersonalPOCToFavoritesCommandImpl perPOCToFavCmd = new AddPersonalPOCToFavoritesCommandImpl();
        perPOCToFavCmd.setUserContext(user);

        final PointOfContactType udaPOC = new PointOfContactType();
        udaPOC.setContactId(1);
        udaPOC.setData("9986359917");
        udaPOC.setDefault(true);
        udaPOC.setDescriptor("Cellphone");
        udaPOC.setMediaType(EnumMediaType.EMAIL);

        perPOCToFavCmd.setPointOfContact(udaPOC);
        cmd.setAddPersonalPOCToFavorites(perPOCToFavCmd);
        req.setCommand(cmd);

        final Executable exe = req.getExecutable();
        Assert.assertTrue(perPOCToFavCmd instanceof AddPersonalPOCToFavoritesCommandImpl);
        exe.execute();
        final SecurityContext basicSecCtx = user.getSecurityContext();
        final ButtonManager butbasMgr = new ButtonManager(basicSecCtx);
        final PersonalPointOfContact dsPOC = PersonalPointOfContactBaseManager
                .NewPersonalPointOfContact();

        dsPOC.setData(udaPOC.getData());
        dsPOC.setMediaType(PersonalPointOfContact.EnumMediaType.valueOf(udaPOC.getMediaType()
                .name()));
        dsPOC.setShortDescriptor(udaPOC.getDescriptor());

        final List<Button> btList = butbasMgr.findByPersonalPointOfContact(dsPOC);
        Assert.assertTrue(btList != null && !btList.isEmpty());

        System.out.println(btList.get(0).getId());
    }

}
