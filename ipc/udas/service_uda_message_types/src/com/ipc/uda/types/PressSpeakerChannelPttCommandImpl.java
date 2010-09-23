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
import com.ipc.uda.types.util.SpeakerUtil;
import com.ipc.uda.types.util.UDAAndDSEntityUtil;

/**
 * @author parkerj
 * @author sharmar
 */
public class PressSpeakerChannelPttCommandImpl extends PressSpeakerChannelPttCommand implements
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
            optSpeaker.get().getCall().udacPressPTT();   
            
            final UserSpeakerChannelManager spkrChnlMgr = new UserSpeakerChannelManager(this.ctx.getSecurityContext());
            try
            {
                /*
                 * Getting Speaker Channel from DS
                 */
                final UserCDI usrCDI = UDAAndDSEntityUtil.getUserCDI(this.ctx);
                final List<UserSpeakerChannel> dsUsrSprList = spkrChnlMgr.getUserSpeakerChannelsFor(usrCDI);
                final UserSpeakerChannel dsSprChnl;
                
                if(dsUsrSprList != null && !dsUsrSprList.isEmpty())
                {
                    dsSprChnl = SpeakerUtil.getUserSpeakerChannelWithInput(dsUsrSprList, this.getSpeakerNumber());
                    if(dsSprChnl != null)
                    {
                        /*
                         * Toggle Speaker Channel Push To talk 
                         */
                        dsSprChnl.setButtonLatch(!dsSprChnl.getButtonLatch());
                    }
                    else
                    {
                        Log.logger().debug(
                                "Speaker object is Null from DS " + this.ctx.getUser().getName());
                        throw new ExecutionException(
                                "UserSpeakerChannel is null for the speaker number "
                                        + this.getSpeakerNumber() + " for user "
                                        + this.ctx.getUserName());
                    }
                    /*
                     * save Speaker Channel Button Latch status
                     */
                    spkrChnlMgr.save(dsSprChnl);
                }
            }
            catch (final Throwable e)
            {
                throw new ExecutionException(
                        "Unable to update speaker channel with speaker number "
                                + this.getSpeakerNumber() + " for user: " + this.ctx.getUserName());
            }
        }
        else
        {
            throw new ExecutionException("Received Press Speaker Channel PTT Command with unknown speaker number : " + this.getSpeakerNumber());
        }
          
        return new Nothing<ExecutionResult>();
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }
}
