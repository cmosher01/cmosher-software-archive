/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.execution;

/**
 * @author mosherc
 */
public class ExecutionException extends Exception
{
    /**
     * 
     */
    public ExecutionException()
    {
        // OK
    }

    /**
     * @param message
     */
    public ExecutionException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public ExecutionException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ExecutionException(String message, Throwable cause)
    {
        super(message,cause);
    }
}
