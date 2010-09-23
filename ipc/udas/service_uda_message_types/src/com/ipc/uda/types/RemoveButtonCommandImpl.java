/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import com.ipc.ds.entity.dto.Button;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.manager.ButtonManager;
import com.ipc.ds.entity.manager.UserCDIManager;
import com.ipc.uda.service.callproc.UdaButton;
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
 * This class is responsible to remove the specified button entry in the database and also update
 * the button sheet application of the user
 * 
 * @author Veena Makam
 * 
 */
public class RemoveButtonCommandImpl extends RemoveButtonCommand implements ExecutableWithContext
{

    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        final ButtonManager buttonMgr = new ButtonManager(this.ctx.getSecurityContext());
        final Optional<UdaButton> udaButton = this.ctx.getCallContext().getButtonSheet().getButton(
                this.getButtonId());
        final UdaButton udaButtonObj = udaButton.get();

        try
        {
            // get the Button object from DS that needs to be removed
            final Button dsButton = buttonMgr.getById(Integer.parseInt(this.getButtonId()
                    .toString()));
            if (dsButton == null)
            {
                Log.logger().debug(
                        "Button entity does not exist in DS for the given Button ID: "
                                + this.getButtonId() + " for user: " + this.ctx.getUserName());
                throw new ExecutionException(
                        "Button entity does not exist in DS for the given Button ID: "
                                + this.getButtonId() + " for user: " + this.ctx.getUserName());
            }

            final UserCDI usrCDI = UDAAndDSEntityUtil.getUserCDI(this.ctx);

            // subscribe for delete events from DS
            DataServicesSubscriptionHelper.createSubscriptionsTo(Button.class.getSimpleName(),
                    dsButton.getId(), this.ctx);

            // remove the buttons from the corresponding UserCDI object
            usrCDI.removeFromButtons(dsButton);

            final UserCDIManager usrCDIMgr = new UserCDIManager(this.ctx.getSecurityContext());

            // save the UserCDI object after the button is removed
            usrCDIMgr.save(usrCDI);

            Log.logger().info(
                    "Removed the Button with ButtonID: " + this.getButtonId() + " for user: "
                            + this.ctx.getUserName());
        }
        catch (final Throwable lException)
        {
            throw new ExecutionException(lException);
        }
        return new Nothing<ExecutionResult>();
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }
}
