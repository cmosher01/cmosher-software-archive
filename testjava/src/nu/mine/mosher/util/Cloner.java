package com.surveysampling.util;

import java.lang.reflect.Method;

/**
 * Provides a static method that will clone any Cloneable Object.
 * This class needs to be in the java.lang package in order to be
 * able to access the protected clone method of Object.
 */
public final class Cloner
{
    private Cloner()
    {
        throw new UnsupportedOperationException();
    }

    public static Cloneable cloneObject(Cloneable cloneableObject) throws CloneNotSupportedException
    {
        try
        {
            Method objClone = cloneableObject.getClass().getMethod("clone",null);
            objClone.setAccessible(true);
            return (Cloneable)objClone.invoke(cloneableObject,null);
        }
        catch (Throwable cause)
        {
            CloneNotSupportedException ex;
            if (cause instanceof CloneNotSupportedException)
            {
                ex = (CloneNotSupportedException)cause;
            }
            else
            {
                ex = new CloneNotSupportedException();
                ex.initCause(cause);
            }
            throw ex;
        }
    }
}
