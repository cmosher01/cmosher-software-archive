/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types.util;

import java.util.List;

import com.ipc.ds.base.exception.InvalidContextException;
import com.ipc.ds.base.exception.StorageFailureException;
import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.ButtonResourceAppearance;
import com.ipc.ds.entity.dto.ResourceAOR;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.manager.ButtonResourceAppearanceManager;
import com.ipc.ds.entity.manager.ResourceAORManager;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.ButtonType;
import com.ipc.uda.types.ButtonUpdatedEvent;
import com.ipc.uda.types.EnumButtonType;
import com.ipc.uda.types.IncomingActionCLITypeEnum;
import com.ipc.uda.types.IncomingActionPriorityTypeEnum;
import com.ipc.uda.types.IncomingActionRingsTypeEnum;
import com.ipc.uda.types.ResourceAorType;
import com.ipc.uda.types.UID;

/**
 * @author Veena Makam
 * 
 */
public class ButtonUtil
{

    /**
     * getButtonResourceAppearance is a helper method which takes a data service entity button and
     * returns the first button resource appearance found associated with that button. In the future
     * this will need to be modified to take into account buttons with multiple button resource
     * appearances
     * 
     * @param button
     * @return The first button resource appearance found in the button.
     * @throws InvalidContextException
     * @throws StorageFailureException
     */
    public static ButtonResourceAppearance getButtonResourceAppearance(final Button button,
            final UserContext ctx) throws InvalidContextException, StorageFailureException
    {
        final ButtonResourceAppearanceManager buttonResourceAppearanceManager = new ButtonResourceAppearanceManager(
                ctx.getSecurityContext());
        final List<ButtonResourceAppearance> buttonResourceAppearanceList = buttonResourceAppearanceManager
                .getResourcesFor(button);

        if (buttonResourceAppearanceList.size() == 0)
        {
            return null;
        }

        return buttonResourceAppearanceList.get(0);
    }

    /**
     * getResourceAor is a helper method which takes a button resource appearance and returns the
     * associated resource AOR entity for the appearance
     * 
     * @param buttonResourceAppearance
     * @return The ResourceAOR associated with the appearance
     * @throws InvalidContextException
     * @throws StorageFailureException
     */
    public static ResourceAOR getResourceAor(
            final ButtonResourceAppearance buttonResourceAppearance, final UserContext ctx)
            throws InvalidContextException, StorageFailureException
    {
        final ResourceAORManager resourceAorManager = new ResourceAORManager(ctx
                .getSecurityContext());

        return resourceAorManager.getResourceAORFor(buttonResourceAppearance);
    }

    /**
     * Populates the ButtonUpdatedEvent that is to be sent to the client
     * 
     * @param usrCtx UserContext
     * @param dsButton Button
     * @return ButtonUpdatedEvent buttonUpdEve
     */
    public static ButtonUpdatedEvent populateButtonUpdatedEvent(final UserContext usrCtx,
            final Button dsButton) throws Exception
    {
        final ButtonUpdatedEvent buttonUpdEve = new ButtonUpdatedEvent();
        final ButtonType button = new ButtonType();
        button.setButtonId(UID.fromDataServicesID(dsButton.getId()));
        button.setButtonNumber(dsButton.getButtonNumber());
        button.setIncomingActionCLI(IncomingActionCLITypeEnum.valueOf(dsButton
                .getIncomingActionCLI().name()));
        button.setIncomingActionPriority(IncomingActionPriorityTypeEnum.valueOf(dsButton
                .getIncomingActionPriority().name()));
        button.setIncomingActionRings(IncomingActionRingsTypeEnum.valueOf(dsButton
                .getIncomingActionRings().name()));
        button.setAutoSignal(dsButton.getAutoSignal());
        button.setKeySequence(dsButton.getKeySequence());
        button.setButtonLabel(dsButton.getButtonLabel());
        button.setButtonType(EnumButtonType.valueOf(dsButton.getButtonType().name()));
        button.setButtonLocked(dsButton.getButtonLock());

        final UserCDI usrCDI = UDAAndDSEntityUtil.getUserCDI(usrCtx);
        button.setAutoHuntEnabled(usrCDI.getAutoHuntEnable());

        // set the resourceAOR for the given UserSpeakerChannel
        final ResourceAORManager resAORMgr = new ResourceAORManager(usrCtx.getSecurityContext());
        final ButtonResourceAppearanceManager resAppMgr = new ButtonResourceAppearanceManager(
                usrCtx.getSecurityContext());

        List<ButtonResourceAppearance> resAppList;
        ResourceAOR dsResAOR;
        // intialize ResourceAOR for the Button
        button.setResourceAor("");
        try
        {
            resAppList = resAppMgr.getResourcesFor(dsButton);
            if (resAppList == null || resAppList.isEmpty())
            {
                Log.logger().debug(
                        "ButtonResourceAppearance is NULL/empty for Button ID: " + dsButton.getId()
                                + " for user: " + usrCtx.getUserName());
            }
            else
            {
                final ButtonResourceAppearance resApp = resAppList.get(0);
                if (resApp != null)
                {
                    button.setAppearance(resApp.getId());
                    dsResAOR = resAORMgr.getResourceAORFor(resApp);
                    if (dsResAOR != null && dsResAOR.getResourceAOR() != null)
                    {
                        button.setResourceAor(dsResAOR.getResourceAOR());
                        button.setResourceAorType(ResourceAorType
                                .valueOf(dsResAOR.getType().name()));
                        button.setInitiateCallOnSeize(dsResAOR.getInitiateCallOnSeize());
                    }
                }
                else
                {
                    Log.logger().debug(
                            "ButtonResourceAppearance is NULL for button ID: " + dsButton.getId()
                                    + " for user: " + usrCtx.getUserName());
                }
            }
        }
        catch (final Throwable lException)
        {
            Log.logger().debug(
                    "Unable to get the ResourceAOR for the Button id: " + dsButton.getId());
        }

        buttonUpdEve.setButton(button);

        return buttonUpdEve;
    }
}
