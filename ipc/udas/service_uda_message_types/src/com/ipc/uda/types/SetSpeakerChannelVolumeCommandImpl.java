/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import java.util.List;

import com.ipc.ds.entity.dto.UserCDI;
import com.ipc.ds.entity.dto.UserSpeakerChannel;
import com.ipc.ds.entity.manager.UserSpeakerChannelManager;
import com.ipc.uda.service.callproc.SpeakerSheet;
import com.ipc.uda.service.callproc.UdaSpeaker;
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
 * @author parkerj
 * @author sharmar
 * @author Veena Makam
 */
public class SetSpeakerChannelVolumeCommandImpl extends SetSpeakerChannelVolumeCommand implements
        ExecutableWithContext
{
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {

        final SpeakerSheet speakerSheet = this.ctx.getCallContext().getSpeakerSheet();

        final Optional<UdaSpeaker> optSpeaker = speakerSheet.getSpeaker(this.getSpeakerNumber());

        if (optSpeaker.exists())
        {
            optSpeaker.get().getCall().udacSetSpeakerChannelVolume(this.getLevel());

            // update DB with the volume for the given speaker channel ID
            final UserSpeakerChannelManager usrSprChlMgr = new UserSpeakerChannelManager(this.ctx.getSecurityContext());
            try
            {
                /*
                 * Getting Speaker Channel from DS
                 */
                final UserCDI usrCDI = UDAAndDSEntityUtil.getUserCDI(this.ctx);
                final List<UserSpeakerChannel> dsUsrSprChlLst = usrSprChlMgr.getUserSpeakerChannelsFor(usrCDI);
                final UserSpeakerChannel dsUsrSprChl;
                
                if (dsUsrSprChlLst != null && !dsUsrSprChlLst.isEmpty())
                {
                    //final UserSpeakerChannel dsUsrSprChl = dsUsrSprChlLst.get(0);
                    dsUsrSprChl = SpeakerUtil.getUserSpeakerChannelWithInput(dsUsrSprChlLst, this.getSpeakerNumber());
                    
                    if (dsUsrSprChl == null)
                    {
                        Log.logger().debug(
                                "Unable to find UserSpeakerChannel from DB for UserSpeakerChannel number: "
                                        + this.getSpeakerNumber() + " for user "
                                        + this.ctx.getUserName());
                        throw new ExecutionException(
                                "Unable to find UserSpeakerChannel from DB for UserSpeakerChannel number: "
                                        + this.getSpeakerNumber() + " for user "
                                        + this.ctx.getUserName());
                    }
                    else
                    {
                        /*
                         * set the volume in UserSpeakerChannel and save Speaker Channel volume
                         */
                        dsUsrSprChl.setVolume(this.getLevel());
                        usrSprChlMgr.save(dsUsrSprChl);
                        Log.logger().debug(
                                "Set speaker channel volume to " + this.getLevel()
                                        + " on the speaker channel number: " + this.getSpeakerNumber()
                                        + " for the user: " + this.ctx.getUserName());
                        DataServicesSubscriptionHelper.createSubscriptionsTo(UserSpeakerChannel.class
                                .getSimpleName(), dsUsrSprChl.getId(), this.ctx);
                    }
                    
                }
                else
                {
                    Log.logger().debug(
                            "Unable to find UserSpeakerChannel from DB for UserSpeakerChannel number: "
                                    + this.getSpeakerNumber() + " for user "
                                    + this.ctx.getUserName());
                    throw new ExecutionException(
                            "Unable to find UserSpeakerChannel from DB for UserSpeakerChannel number: "
                                    + this.getSpeakerNumber() + " for user "
                                    + this.ctx.getUserName());
                }
            }
            catch (final Throwable lException)
            {
                throw new ExecutionException(lException);
            }
        }
        else
        {
            throw new ExecutionException(
                    "Received Set Speaker Channel Volume Command with unknown speaker number: "
                            + this.getSpeakerNumber());
        }

        return new Nothing<ExecutionResult>();
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }
}
