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
import com.ipc.ds.entity.dto.PointOfContact;
import com.ipc.ds.entity.internal.manager.base.PointOfContactBaseManager;
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
public class AddInstancePOCToFavoritesCommandImplTest extends TestCase
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
     * Test method for {@link com.ipc.uda.types.AddInstancePOCToFavoritesCommandImpl#execute()}.
     * 
     * @throws ExecutionException
     * @throws StorageFailureException
     * @throws InvalidContextException
     */
    @Test
    public void testExecute() throws ExecutionException, InvalidContextException,
            StorageFailureException
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
        final AddInstancePOCToFavoritesCommandImpl insPOCToFavCmd = new AddInstancePOCToFavoritesCommandImpl();
        insPOCToFavCmd.setUserContext(user);

        final PointOfContactType udaPOC = new PointOfContactType();
        udaPOC.setContactId(1);
        udaPOC.setData("9986359917");
        udaPOC.setDefault(true);
        udaPOC.setDescriptor("Cellphone");
        udaPOC.setMediaType(EnumMediaType.ICM);

        insPOCToFavCmd.setPointOfContact(udaPOC);
        cmd.setAddInstancePOCToFavorites(insPOCToFavCmd);
        req.setCommand(cmd);

        final Executable exe = req.getExecutable();
        Assert.assertTrue(insPOCToFavCmd instanceof AddInstancePOCToFavoritesCommandImpl);
        exe.execute();
        final SecurityContext basicSecCtx = user.getSecurityContext();
        final ButtonManager butbasMgr = new ButtonManager(basicSecCtx);
        final PointOfContact dsPOC = PointOfContactBaseManager.NewPointOfContact();

        dsPOC.setData(udaPOC.getData());
        dsPOC.setMediaType(PointOfContact.EnumMediaType.valueOf(udaPOC.getMediaType().name()));
        dsPOC.setShortDescriptor(udaPOC.getDescriptor());

        final List<Button> btList = butbasMgr.findByPointOfContact(dsPOC);
        Assert.assertTrue(btList != null && !btList.isEmpty());

        System.out.println(btList.get(0).getId());

    }

}
