/*
 * Created on April 15, 2005
 */
package com.surveysampling.util.key.exception;

/**
 * Error trying to create the <code>DatalessKeyAccess</code>
 * class for the given underlying type.
 * 
 * @author Chris Mosher
 */
public class DatalessKeyAccessCreationException extends RuntimeException
{
    /**
     * 
     */
    public DatalessKeyAccessCreationException()
    {
        super("Error creating DatalessKeyAccess class");
    }

    /**
     * @param datalessKeyUnderlyingTypeName
     */
    public DatalessKeyAccessCreationException(final String datalessKeyUnderlyingTypeName)
    {
        super("Error creating DatalessKeyAccess"+datalessKeyUnderlyingTypeName+" class");
    }

    /**
     * @param datalessKeyUnderlyingTypeName
     * @param cause
     */
    public DatalessKeyAccessCreationException(final String datalessKeyUnderlyingTypeName, final Throwable cause)
    {
        super("Error creating DatalessKeyAccess"+datalessKeyUnderlyingTypeName+" class",cause);
    }

    /**
     * @param cause
     */
    public DatalessKeyAccessCreationException(final Throwable cause)
    {
        super("Error creating DatalessKeyAccess class",cause);
    }
}
