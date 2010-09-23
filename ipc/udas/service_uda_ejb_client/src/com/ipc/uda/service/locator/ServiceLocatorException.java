/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.locator;



/**
 * @author mordarsd
 * 
 */
public class ServiceLocatorException extends RuntimeException
{

    /**
     * 
     */
    public ServiceLocatorException()
    {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public ServiceLocatorException(String message, Throwable cause)
    {
        super(message,cause);
    }

    /**
     * @param message
     */
    public ServiceLocatorException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public ServiceLocatorException(Throwable cause)
    {
        super(cause);
    }

}
