/**
 * 
 */
package com.ipc.uda.service.util;

/**
 * Represents an exceptional environment within the UDA domain.
 * 
 * 
 * @author mordarsd
 * @author mosherc
 */
public class UdaEnvironmentException extends IllegalStateException
{

    /**
     * 
     */
    public UdaEnvironmentException()
    {
    }

    /**
     * @param message
     * @param cause
     */
    public UdaEnvironmentException(String message, Throwable cause)
    {
        super(message,cause);
    }

    /**
     * @param message
     */
    public UdaEnvironmentException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public UdaEnvironmentException(Throwable cause)
    {
        super(cause);
    }

    
}
