/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.ejb.session;



import java.lang.reflect.InvocationTargetException;

import javax.ejb.Local;
import javax.ejb.LocalHome;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionHandler;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.execution.invocation.AsynchronousInvoker;
import com.ipc.uda.service.execution.invocation.Invoker;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;

import commonj.work.WorkManager;



/**
 * Handles the execution of a given {@link Executable} command.
 */
@Stateless
@Local( { ExecutionHandlerLocal.class })
@LocalHome(ExecutionHandlerLocalHome.class)
public class CommandExecutionHandlerBean implements ExecutionHandler
{
    private static final String JNDI_WORK_MANAGER = "java:comp/env/wm/CommandWorkManager";

    private final Invoker invoker;

    /**
     * Default bean constructor
     * 
     * @throws NamingException
     */
    public CommandExecutionHandlerBean()
    {
        Context ctx = null;
        try
        {
            ctx = new InitialContext();

            final WorkManager workMgr = lookUpWorkManager(ctx);
            this.invoker = new AsynchronousInvoker(workMgr);
        }
        catch (final Throwable e)
        {
            throw new IllegalStateException("Error instantiating CommandExecutionHandlerBean",e);
        }
        finally
        {
            if (ctx != null)
            {
                try
                {
                    ctx.close();
                }
                catch (final Throwable e)
                {
                    Log.logger().info("error while closing context",e);
                }
            }
        }
    }

    /**
     * Looks up our work manager in the given JNDI context.
     * @param ctx
     * @return
     * @throws NamingException
     */
    private static WorkManager lookUpWorkManager(final Context ctx) throws NamingException
    {
        return (WorkManager)ctx.lookup(CommandExecutionHandlerBean.JNDI_WORK_MANAGER);
    }

    /**
     * Executes the given {@link Executable} command, asynchronously. This method will
     * "fire and forget," that is, it will schedule the command to run at some time in the future,
     * and will return immediately after scheduling it. It will not block waiting for the command to
     * be run.
     * 
     * @param executable
     *            the {@link Executable} command to execute
     * @return an {@link Optional} that does not exist
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Optional<ExecutionResult> execute(final Executable executable)
            throws InvocationTargetException
    {
        return this.invoker.invoke(executable);
    }
}
