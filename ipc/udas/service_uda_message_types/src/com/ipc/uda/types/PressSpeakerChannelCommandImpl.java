/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import com.ipc.ds.base.security.SecurityContext;
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

/**
 * @author parkerj
 * @author sharmar
 * 
 */
public class PressSpeakerChannelCommandImpl extends PressSpeakerChannelCommand implements
        ExecutableWithContext
{
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {

        final SpeakerSheet speakerSheet = this.ctx.getCallContext().getSpeakerSheet();
        final Optional<UdaSpeaker> optSpeaker = speakerSheet.getSpeaker(this.getSpeakerNumber());
        if (!optSpeaker.exists())
        {
            throw new ExecutionException(
                    "Received pressSpeaker command with unknown speaker channel number: "
                            + this.getSpeakerNumber());
        }

        try
        {
            final UdaSpeaker speaker = optSpeaker.get();

            speaker.getCall().udacPressSpeakerChannel();

            final UserSpeakerChannel dsSprChnl = speaker.getDsSpeaker();
            dsSprChnl.setActiveStatus(true);

            final SecurityContext basicSecContext = this.ctx.getSecurityContext();
            final UserSpeakerChannelManager spkrChnlMgr =
                new UserSpeakerChannelManager(basicSecContext);

            spkrChnlMgr.save(dsSprChnl);
        }
        catch (final Throwable e)
        {
            throw new ExecutionException(
                    "Unable to update speaker channel with speaker number "
                            + this.getSpeakerNumber() + " for user: " + this.ctx.getUserName());
        }

        return new Nothing<ExecutionResult>();
    }

    @Override
    public void setUserContext(final UserContext unused)
    {
        this.ctx = unused;
    }
}
