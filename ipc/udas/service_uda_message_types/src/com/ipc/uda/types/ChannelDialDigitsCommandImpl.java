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
 * @author sharmar
 * 
 */
public class ChannelDialDigitsCommandImpl extends ChannelDialDigitsCommand implements ExecutableWithContext
{
	private UserContext ctx;
	 
    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {   
        final SpeakerSheet speakerSheet = this.ctx.getCallContext().getSpeakerSheet();
        
        final Optional<UdaSpeaker> optSpeaker = speakerSheet.getSpeaker(this.getSpeakerNumber());

        if (optSpeaker.exists())
        {
            optSpeaker.get().getCall().udacDialDigits(this.getDigits());         
        }
        else
        {
            throw new ExecutionException("Received Channel DialDigits Command  with unknown speaker number : " + this.getSpeakerNumber());
        }
        
        return new Nothing<ExecutionResult>();
    }

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }
}
