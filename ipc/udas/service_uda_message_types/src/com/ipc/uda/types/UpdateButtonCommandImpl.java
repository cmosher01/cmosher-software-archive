/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.ButtonResourceAppearance;
import com.ipc.ds.entity.dto.PersonalPointOfContact;
import com.ipc.ds.entity.dto.PointOfContact;
import com.ipc.ds.entity.dto.ResourceAOR;
import com.ipc.ds.entity.dto.Button.EnumIcon;
import com.ipc.ds.entity.dto.Button.EnumIncomingActionCLI;
import com.ipc.ds.entity.dto.Button.EnumIncomingActionPriority;
import com.ipc.ds.entity.dto.Button.EnumIncomingActionRings;
import com.ipc.ds.entity.internal.manager.base.ButtonResourceAppearanceBaseManager;
import com.ipc.ds.entity.manager.ButtonManager;
import com.ipc.ds.entity.manager.ButtonResourceAppearanceManager;
import com.ipc.ds.entity.manager.PersonalPointOfContactManager;
import com.ipc.ds.entity.manager.PointOfContactManager;
import com.ipc.ds.entity.manager.ResourceAORManager;
import com.ipc.uda.service.callproc.UdaButton;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;

/**
 * This class is responsible to update the button entities in the database and make corresponding
 * updates in the ButtonSheet application of the user
 * 
 * @author Veena Makam
 * 
 */
public class UpdateButtonCommandImpl extends UpdateButtonCommand implements ExecutableWithContext
{

    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        final Optional<UdaButton> udaButton = this.ctx.getCallContext().getButtonSheet().getButton(
                this.getButtonId());
        final UdaButton udaButtonObj = udaButton.get();

        try
        {
            final ButtonManager buttonMgr = new ButtonManager(this.ctx.getSecurityContext());
            final Button dsButton = buttonMgr.getById(Integer.parseInt(this.getButtonId()
                    .toString()));
            DataServicesSubscriptionHelper.createSubscriptionsTo(Button.class.getSimpleName(),
                    dsButton.getId(), this.ctx);

            updateButton(dsButton);

            // TODO shouldn't this really be in ButtonSheetNotificationListener, and
            // shouldn't it remove the button from the button sheet before re-adding it?
            // Add the button object to ButtonSheetApplication
            this.ctx.getCallContext().getButtonSheet().addButtonFromDsButton(dsButton,
                    udaButtonObj.isFixed(), false);

            // save the updated button to DS
            Log.logger().debug(
                    "Updated Button in the DS for button ID: " + this.getButtonId() + " for user: "
                            + this.ctx.getUserName());

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

    /**
     * Validates the input received and udpates the button object
     * 
     * @param dsButton Button
     * @throws ExecutionException
     */
    private void updateButton(final Button dsButton) throws ExecutionException
    {
        try
        {
            // update all the button attributes received from client
            dsButton.setButtonLabel(this.getButtonLabel());
            dsButton.setButtonNumber(this.getButtonNumber());
            dsButton.setIcon(EnumIcon.valueOf(this.getIcon().value()));
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

            // only if the button type is Resource/Resource_And_Speed_Dial, resourceAOR and
            // appearance is updated
            // hence determine the button type of the given button id
            final EnumButtonType dsButtonType = EnumButtonType.fromValue(dsButton.getButtonType()
                    .name());
            if (dsButtonType == EnumButtonType.RESOURCE
                    || dsButtonType == EnumButtonType.RESOURCE_AND_SPEED_DIAL)
            {
                // find the ResourceAOR with the given input
                final ResourceAORManager resAORMgr = new ResourceAORManager(this.ctx
                        .getSecurityContext());
                final ResourceAOR resAORObj = resAORMgr.findByResourceAOREqualing(this
                        .getResourceAor());

                // validate resource aor from DS
                if (resAORObj == null)
                {
                    Log.logger().debug(
                            "ResourceAOR: " + this.getResourceAor()
                                    + " provided by UDAC is invalid for the user: "
                                    + this.ctx.getUserName() + " for the button ID: "
                                    + this.getButtonId());
                    throw new ExecutionException("ResourceAOR: " + this.getResourceAor()
                            + " provided by UDAC is invalid for the user: "
                            + this.ctx.getUserName() + " for the button ID: " + this.getButtonId());
                }

                updateResourceAppearances(resAORObj, dsButton);
            }
        }
        catch (final Throwable lException)
        {
            throw new ExecutionException(lException);
        }
    }

    /**
     * Validates and updates ResourceAppearances of the given ResourceAOR
     * 
     * @param resAORObj
     * @param dsButton
     * @throws ExecutionException
     */
    private void updateResourceAppearances(final ResourceAOR resAORObj, final Button dsButton)
            throws ExecutionException
    {
        try
        {
            // determine the max appearances of the resource aor
            final int maxAppearances = resAORObj.getMaxAppearances();
            Log.logger().debug("MaxAppearances for the given ResourceAOR is: " + maxAppearances);

            // validate the resource appearance given as input
            if (this.getAppearance() > maxAppearances)
            {
                Log.logger().debug(
                        "Invalid appearance provided by UDAC for the button ID: "
                                + this.getButtonId() + " for the user: " + this.ctx.getUserName());
                throw new ExecutionException(
                        "Invalid appearance provided by UDAC for the button ID: "
                                + this.getButtonId() + " for the user: " + this.ctx.getUserName());
            }
            final ButtonManager buttonMgr = new ButtonManager(this.ctx.getSecurityContext());
            // determine the resource appearances of the given resource aor
            final ButtonResourceAppearanceManager buttonResAppMgr = new ButtonResourceAppearanceManager(
                    this.ctx.getSecurityContext());
            final List<ButtonResourceAppearance> buttonResAppList = buttonResAppMgr
                    .findByResourceAOR(resAORObj);

            // validate resource appearance list of the button entity
            // if no appearances are present, create one and add to the button entity
            if (buttonResAppList == null || buttonResAppList.isEmpty())
            {
                Log.logger().debug(
                        "There are no button resource appearances for the resource aor: "
                                + this.getResourceAor());
                final ButtonResourceAppearance buttonResApp = ButtonResourceAppearanceBaseManager
                        .NewButtonResourceAppearance();
                buttonResApp.setAppearance(this.getAppearance());
                buttonResApp.setResourceAOR(resAORObj);
                // FIXME: determine how to get the sequence for the buttonresourceappearance
                // buttonResApp.setSequence(sequence);
                dsButton.addToResources(buttonResApp);
                buttonMgr.save(dsButton);
                buttonResAppMgr.save(buttonResApp);
            }
            else
            // update the button resource appearance
            {
                for (final ButtonResourceAppearance buttonResApp : buttonResAppList)
                {
                    if (buttonResApp.getAppearance() == this.getAppearance())
                    {
                        buttonResApp.setResourceAOR(resAORObj);
                        buttonMgr.save(dsButton);
                        buttonResAppMgr.save(buttonResApp);
                    }
                }
            }
        }
        catch (final Throwable lException)
        {
            throw new ExecutionException(lException);
        }
    }
}
