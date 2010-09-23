/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;



import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.types.util.LifecycleUtil;
import com.ipc.uda.types.util.UserStateChangeException;



/**
 * This class is responsible for logging messages sent from client
 * 
 * @author Jagannadham Dulipala
 * 
 */
public class ForceLogOffCommandImpl extends ForceLogOffCommand implements ExecutableWithContext
{
	 private UserContext ctx;
    /**
     * Force Logoff the user.
     */
    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        
        // REVIEW 0 invalid code formatting 
    	try {
			LifecycleUtil.forceLogOff(ctx);
		} 
    	catch (final Throwable e)
    	{
			throw new ExecutionException(
                    "Unexpected Exception caught while attempting to perform User FORCED LOGOFF: reason: " +
                    e.getMessage(), e);
		}
        return new Nothing<ExecutionResult>();
    }
    
    @Override
    public void setUserContext(UserContext unused)
    {
        // context not needed for this class
    	ctx = unused;
    	
    }
}
