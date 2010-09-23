/**
 * 
 */
package com.ipc.uda.service.lifecycle;

/**
 * @author mosherc
 *
 */
public class LifecycleException extends Exception
{
    /**
     * 
     */
    public LifecycleException()
    {
        // OK
    }

    /**
     * @param message
     */
    public LifecycleException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public LifecycleException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public LifecycleException(String message, Throwable cause)
    {
        super(message,cause);
    }

}
