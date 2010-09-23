/**
 * 
 */
package com.ipc.uda.types;

import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;

/**
 * @author parkerj
 *
 */
public class PressMuteAllSpeakerChannelsCommandImpl extends PressMuteAllSpeakerChannelsCommand implements ExecutableWithContext
{
	private UserContext ctx;
	 
    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
    	 return new Nothing<ExecutionResult>();
    }

    @Override
    public void setUserContext(UserContext unused)
    {
        ctx = unused;
    }
}
