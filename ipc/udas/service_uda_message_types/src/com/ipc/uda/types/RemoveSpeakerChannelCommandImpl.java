/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import com.ipc.ds.base.security.SecurityContext;
import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.ds.entity.manager.UserCDIManager;
import com.ipc.ds.entity.manager.UserSpeakerChannelManager;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.util.DataServicesSubscriptionHelper;
import com.ipc.uda.types.util.SpeakerUtil;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * Removes the speaker channel data from the data base for the given channel ID
 * 
 * @author Veena Makam
 * 
 */
public class RemoveSpeakerChannelCommandImpl extends RemoveSpeakerChannelCommand implements
        ExecutableWithContext
{
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        // get the basic security context object
        final SecurityContext basicSecContext = this.ctx.getSecurityContext();

        // relevant manager objects
        final UserSpeakerChannelManager usrSprChlMgr = new UserSpeakerChannelManager(
                basicSecContext);
        final UserCDIManager usrCdiMgr = new UserCDIManager(basicSecContext);

        try
        {
            final UserCDI usrCDI = UDAAndDSEntityUtil.getUserCDI(this.ctx);
            final List<UserSpeakerChannel> dsUsrSprList = usrSprChlMgr
                    .getUserSpeakerChannelsFor(usrCDI);
            if (dsUsrSprList != null && !dsUsrSprList.isEmpty())
            {
                final UserSpeakerChannel dsUsrSpr = SpeakerUtil.getUserSpeakerChannelWithInput(
                        dsUsrSprList, this.getSpeakerNumber());
                if (dsUsrSpr != null)
                {

                    // subscribe for delete events from DS
                    DataServicesSubscriptionHelper.createSubscriptionsTo(UserSpeakerChannel.class
                            .getSimpleName(), dsUsrSpr.getId(), this.ctx);

                    usrCDI.removeFromUserSpeakerChannels(dsUsrSpr);
                    usrCdiMgr.save(usrCDI);
                    Log.logger().debug(
                            "Removed the UserSpeakerChannel with channel number: "
                                    + this.getSpeakerNumber() + " from database for user "
                                    + this.ctx.getUserName());
                }
                else
                {
                    Log.logger().info(
                            "Unable to Removed the UserSpeakerChannel with channel number: "
                                    + this.getSpeakerNumber()
                                    + " from database; This UserSpeakerChannel does not exist!");
                    throw new ExecutionException(
                            "UserSpeakerChannel is null for the speaker number "
                                    + this.getSpeakerNumber() + " for user "
                                    + this.ctx.getUserName());
                }
            }
            else
            {
                Log.logger().info(
                        "Unable to Removed the UserSpeakerChannel with channel number: "
                                + this.getSpeakerNumber()
                                + " from database; This UserSpeakerChannel does not exist!");
                throw new ExecutionException("UserSpeakerChannel is null for the speaker number "
                        + this.getSpeakerNumber() + " for user " + this.ctx.getUserName());
            }
        }
        catch (final Throwable lException)
        {
            throw new ExecutionException(lException);
        }

        return new Nothing<ExecutionResult>();
    }

    @Override
    public void setUserContext(final UserContext unused)
    {
        this.ctx = unused;
    }
}
