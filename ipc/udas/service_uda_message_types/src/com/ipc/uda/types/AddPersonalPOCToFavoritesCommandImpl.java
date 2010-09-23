/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import javax.xml.bind.annotation.XmlTransient;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.PersonalContact;
import com.ipc.ds.entity.dto.PersonalPointOfContact;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.internal.manager.base.ButtonBaseManager;
import com.ipc.ds.entity.internal.manager.base.PersonalPointOfContactBaseManager;
import com.ipc.ds.entity.manager.ButtonManager;
import com.ipc.ds.entity.manager.PersonalContactManager;
import com.ipc.ds.entity.manager.PersonalPointOfContactManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * @author Veena Makam
 * 
 */
public class AddPersonalPOCToFavoritesCommandImpl extends AddPersonalPOCToFavoritesCommand
        implements ExecutableWithContext
{
    @XmlTransient
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        final PointOfContactType udaPOC = this.getPointOfContact();
        final SecurityContext basSecCtx = this.ctx.getSecurityContext();

        final ButtonManager buttonMgr = new ButtonManager(basSecCtx);

        if (buttonMgr == null)
        {
            Log.logger().debug(
                    "ButtonManager is Null from DS for user " + this.ctx.getUser().getName());
            return returnNothing();
        }
        final Button dsButton = ButtonBaseManager.NewButton();

        final PersonalPointOfContactManager pocMgr = new PersonalPointOfContactManager(basSecCtx);
        if (pocMgr == null)
        {
            Log.logger().debug(
                    "PersonalPointOfContactManager is Null from DS for user "
                            + this.ctx.getUser().getName());
            return returnNothing();
        }
        PersonalPointOfContact dsPOC = PersonalPointOfContactBaseManager
                .NewPersonalPointOfContact();

        final PersonalContactManager ctManager = new PersonalContactManager(basSecCtx);
        if (ctManager == null)
        {
            System.out.println("PersonalContactManager is Null from DS for user "
                    + this.ctx.getUser().getName());
            return returnNothing();
        }
        try
        {
            // maps the udaPOC type to dsPOC
            dsPOC = UDAAndDSEntityUtil.mapUDAToDSPOC(udaPOC, dsPOC);
            final PersonalContact dsContact = ctManager.getById(this.getPointOfContact()
                    .getContactId());
            if (dsContact == null)
            {
                Log.logger().debug(
                        "Unable to find Contact from DB for contact ID: "
                                + this.getPointOfContact().getContactId());
                throw new ExecutionException("Unable to find Contact from DB for contact ID: "
                        + this.getPointOfContact().getContactId());
            }
            pocMgr.save(dsPOC);
            dsContact.addToPersonalPointOfContacts(dsPOC);
            ctManager.save(dsContact);
            dsButton.setPersonalPointOfContact(dsPOC);

            final UserCDI userCDI = UDAAndDSEntityUtil.getUserCDI(this.ctx);
            userCDI.addToButtons(dsButton);

            Log.logger().debug(
                    "Button entity created on the database with button number: "
                            + dsButton.getButtonNumber());

            // Add the button object to ButtonSheetApplication
            this.ctx.getCallContext().getButtonSheet()
                    .addButtonFromDsButton(dsButton, false, false);

            // Subscribe for buttonAddedEvent
            DataServicesSubscriptionHelper.createSubscriptionsTo(Button.class.getSimpleName(),
                    dsButton.getId(), this.ctx);
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(e);
        }

        return returnNothing();
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }

    private Optional<ExecutionResult> returnNothing()
    {
        return new Nothing<ExecutionResult>();
    }

}
