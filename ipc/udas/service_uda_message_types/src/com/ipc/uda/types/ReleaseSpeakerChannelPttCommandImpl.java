/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

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
 */
public class ReleaseSpeakerChannelPttCommandImpl extends ReleaseSpeakerChannelPttCommand implements
        ExecutableWithContext
{
    private UserContext ctx;

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        
        final SpeakerSheet speakerSheet = this.ctx.getCallContext().getSpeakerSheet();
        
        //TODO why channelID is UID and not int in xsd
          final Optional<UdaSpeaker> optSpeaker = speakerSheet.getSpeaker(this.getSpeakerNumber());

          if (optSpeaker.exists())
          {
              optSpeaker.get().getCall().udacReleasePTT();
          }
          else
          {
              throw new ExecutionException("Received Release Speaker Channel PTT Command with unknown channal ID: " + this.getSpeakerNumber());
          }
          
        return new Nothing<ExecutionResult>();
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }
}
