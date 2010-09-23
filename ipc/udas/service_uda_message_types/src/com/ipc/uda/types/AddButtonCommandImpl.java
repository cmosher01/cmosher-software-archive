/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.ButtonResourceAppearance;
import com.ipc.ds.entity.dto.PersonalPointOfContact;
import com.ipc.ds.entity.dto.PointOfContact;
import com.ipc.ds.entity.dto.ResourceAOR;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.dto.Button.EnumIncomingActionCLI;
import com.ipc.ds.entity.dto.Button.EnumIncomingActionPriority;
import com.ipc.ds.entity.dto.Button.EnumIncomingActionRings;
import com.ipc.ds.entity.internal.manager.base.ButtonBaseManager;
import com.ipc.ds.entity.manager.PersonalPointOfContactManager;
import com.ipc.ds.entity.manager.PointOfContactManager;
import com.ipc.ds.entity.manager.UserCDIManager;
import com.ipc.uda.event.ExecutableResultQueue;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.ButtonUtil;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * This class is responsible to add the button to the DS on receiving a command from the client
 * 
 * @author Veena Makam
 * 
 */
public class AddButtonCommandImpl extends AddButtonCommand implements ExecutableWithContext
{

    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        // REVIEW: 10503 This impl needs to be updated to match the current xsd changes, resourceAor
        // & appearance come from the buttons resourceAor

        final Button dsButton = ButtonBaseManager.NewButton();

        try
        {
            populateDSButton(dsButton);
            final ButtonResourceAppearance buttonResApp = ButtonUtil.getButtonResourceAppearance(
                    dsButton, this.ctx);
            final ResourceAOR resAOR = ButtonUtil.getResourceAor(buttonResApp, this.ctx);
            if (resAOR == null)
            {
                Log.logger().debug(
                        "ResourceAOR is null from DS for resourceAOR: " + this.getResourceAor()
                                + " of user: " + this.ctx.getUserName());
            }
            else
            {
                dsButton.addToResources(buttonResApp);
            }
            final UserCDI userCDI = UDAAndDSEntityUtil.getUserCDI(this.ctx);
            userCDI.addToButtons(dsButton);
            final UserCDIManager dsCDIMgr = new UserCDIManager(this.ctx.getSecurityContext());
            dsCDIMgr.save(userCDI);
            Log.logger().debug(
                    "New Button added to the database with Button ID: " + dsButton.getId());

            // Add the button object to ButtonSheetApplication
            this.ctx.getCallContext().getButtonSheet()
                    .addButtonFromDsButton(dsButton, false, false);

            // Send the button updated event after the new button is added to the db in order to
            // notify the client of the succesful addition of the button
            final ButtonUpdatedEvent buttonUpdEve = ButtonUtil.populateButtonUpdatedEvent(this.ctx,
                    dsButton);
            final Event eve = new Event();
            eve.setButtonUpdated(buttonUpdEve);
            ExecutableResultQueue.<Event> send(eve, this.ctx.getUser(), this.ctx.getUserID()
                    .getDeviceID());

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

    /**
     * Populates Button object with the input parameters.
     * 
     * @param dsButton Button
     * @throws Throwable
     */
    private void populateDSButton(final Button dsButton) throws Throwable
    {
        // Button entity in DS has a field uiName that is mandatory. This field is used by DMS only
        // we can ignore it, but unfortunately we will need to populate something in the database
        // for testing.
        final String defaultUIName = "uiName";

        try
        {
            dsButton.setUiName(defaultUIName);
            dsButton.setButtonLabel(this.getButtonLabel());
            dsButton.setButtonNumber(this.getButtonNumber());
            dsButton.setIcon(Button.EnumIcon.valueOf(this.getIcon().value()));
            dsButton.setIncomingActionCLI(EnumIncomingActionCLI.valueOf(this.getIncomingActionCLI()
                    .value()));
            dsButton.setIncomingActionPriority(EnumIncomingActionPriority.valueOf(this
                    .getIncomingActionPriority().value()));
            dsButton.setIncomingActionRings(EnumIncomingActionRings.valueOf(this
                    .getIncomingActionRings().value()));
            dsButton.setKeySequence(this.getKeySequence());
            dsButton.setAutoSignal(this.isAutoSignal());
            dsButton.setButtonType(Button.EnumButtonType.valueOf(this.getButtonType().value()));
            dsButton.setDestination(this.getDestination());
            dsButton.setIncludeInCallHistory(this.isIncludeInCallHistory());

            // check for null in case of optional fields
            if (this.getDirectoryContactType() != null && this.getPointOfContactId() != null)
            {
                // check if the Button belongs to personal/instance category type
                if (this.getDirectoryContactType() == DirectoryContactType.PERSONAL)
                {
                    final PersonalPointOfContactManager perPOCMgr = new PersonalPointOfContactManager(
                            this.ctx.getSecurityContext());
                    final PersonalPointOfContact perPOC = perPOCMgr.getById(Integer.parseInt(this
                            .getPointOfContactId().toString()));
                    dsButton.setPersonalPointOfContact(perPOC);
                }
                else
                {
                    final PointOfContactManager pocMgr = new PointOfContactManager(this.ctx
                            .getSecurityContext());
                    final PointOfContact dsPOC = pocMgr.getById(Integer.parseInt(this
                            .getPointOfContactId().toString()));
                    dsButton.setPointOfContact(dsPOC);
                }
            }
        }
        catch (final Throwable lException)
        {
            throw lException;
        }
    }

    private Optional<ExecutionResult> returnNothing()
    {
        return new Nothing<ExecutionResult>();
    }

}
