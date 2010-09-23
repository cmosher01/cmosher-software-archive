/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.execution.registry;



import com.ipc.uda.service.execution.ExecutionHandler;



/**
 * Holds classes to use for command and query {@link ExecutionHandler}s.
 * @author mordarsd
 * @author mosherc
 */
public class ExecutionHandlerRegistry
{
    // single instance of this class
    private static final ExecutionHandlerRegistry instance = new ExecutionHandlerRegistry();

    private Class<? extends ExecutionHandler> commandExecutionHandlerImpl;
    private Class<? extends ExecutionHandler> queryExecutionHandlerImpl;



    /**
     * Initializes this registry.
     */
    ExecutionHandlerRegistry()
    {
        // this is a singleton
        // it has package-level access so unit tests can instantiate, too
    }

    /**
     * Gets the single, static instance of {@link ExecutionHandlerRegistry}
     * @return singleton instance
     */
    public static ExecutionHandlerRegistry getInstance()
    {
        return instance;
    }





    /**
     * Creates a new instance of the class currently registered to execute commands.
     * @return new {@link ExecutionHandler} for commands
     */
    public ExecutionHandler createCommandExecutionHandler()
    {
        try
        {
            return this.commandExecutionHandlerImpl.newInstance();
        }
        catch (final Throwable programmingError)
        {
            throw new IllegalStateException(programmingError);
        }
    }

    /**
     * Creates a new instance of the class currently registered to execute queries.
     * @return new {@link ExecutionHandler} for queries
     */
    public ExecutionHandler createQueryExecutionHandler()
    {
        try
        {
            return this.queryExecutionHandlerImpl.newInstance();
        }
        catch (final Throwable programmingError)
        {
            throw new IllegalStateException(programmingError);
        }
    }





    /**
     * Registers the given class to be used to create command handlers.
     * @param newHandler
     */
    void setCommandExecutionHandlerClass(final Class<? extends ExecutionHandler> newHandler)
    {
        verifyHanlderCanBeInstantiated(newHandler);
        this.commandExecutionHandlerImpl = newHandler;
    }

    /**
     * Registers the given class to be used to create query handlers.
     * @param newHandler
     */
    void setQueryExecutionHandlerClass(final Class<? extends ExecutionHandler> newHandler)
    {
        verifyHanlderCanBeInstantiated(newHandler);
        this.queryExecutionHandlerImpl = newHandler;
    }



    private static void verifyHanlderCanBeInstantiated(final Class<? extends ExecutionHandler> newHandler) throws IllegalStateException
    {
        try
        {
            final ExecutionHandler test = newHandler.newInstance();
            if (test == null)
            {
                throw new IllegalStateException("cannot instantiate ExecutionHandler");
            }
        }
        catch (final Throwable e)
        {
            throw new IllegalStateException("error trying to instantiate ExecutionHandler",e);
        }
    }
}
