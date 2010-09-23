/* Copyright (c) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;



import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;



/**
 * This class is responsible for logging messages sent from client
 * 
 * @author Bhavya Bhat
 * 
 */
public class LogMessageCommandImpl extends LogMessageCommand implements ExecutableWithContext
{
    /**
     * Logs a message from UDAC to the UDAS logger.
     */
    @Override
    public Optional<ExecutionResult> execute()
    {
        for (final LogMessageType msg : this.logMessage)
        {
            final StringBuilder strBuf = new StringBuilder();

            strBuf.append(msg.level.name());
            strBuf.append(" ");
            strBuf.append(msg.message);
            strBuf.append(" ");
            strBuf.append(msg.stackTrace);

            // TODO not sure what level to log UDAC messages as
            // (debug, info, or is it an event instead)
            Log.logger().info(strBuf.toString());
        }
        return new Nothing<ExecutionResult>();
    }

    @Override
    public void setUserContext(UserContext unused)
    {
        // context not needed for this class
    }
}
