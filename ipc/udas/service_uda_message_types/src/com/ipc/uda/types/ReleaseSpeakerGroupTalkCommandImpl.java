/**
 * 
 */
package com.ipc.uda.types;

import com.ipc.uda.service.callproc.SpeakerSheet;
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
public class ReleaseSpeakerGroupTalkCommandImpl extends ReleaseSpeakerGroupTalkCommand implements ExecutableWithContext
{
	private UserContext ctx;
	 
    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        final SpeakerSheet speakerSheet = this.ctx.getCallContext().getSpeakerSheet();
        switch (this.group)
        {
            case GROUP_1:
            {
                speakerSheet.releaseGroupPtt(1);
            }
            break;
            case GROUP_2:
            {
                speakerSheet.releaseGroupPtt(2);
            }
            break;
        }
        
        return new Nothing<ExecutionResult>();
    }
    
    @Override
    public void setUserContext(UserContext ctx)
    {
        this.ctx = ctx;
    }
}
