package com.surveysampling.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Provides a static method that will clone any Cloneable Object.
 * This class reflection in order to be
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
            return clone(cloneableObject, getCloneMethod(cloneableObject.getClass()));
        }
        catch (CloneNotSupportedException e)
        {
            throw e;
        }
        catch (Throwable cause)
        {
            CloneNotSupportedException ex;
            ex = new CloneNotSupportedException();
            ex.initCause(cause);
            throw ex;
        }
    }

    static Cloneable clone(Cloneable cloneableObject, Method methodClone)
        throws IllegalArgumentException, IllegalAccessException, CloneNotSupportedException, InvocationTargetException
    {
        Cloneable clon;
        try
        {
            clon = (Cloneable)methodClone.invoke(cloneableObject, null);
        }
        catch (InvocationTargetException e)
        {
            Throwable cause = e.getCause();
            if (cause instanceof CloneNotSupportedException)
            {
                throw (CloneNotSupportedException)cause;
            }
            else
            {
                throw e;
            }
        }
        return clon;
    }

    static Method getCloneMethod(Class cl) throws SecurityException, NoSuchMethodException
    {
        Method methodClone;
        methodClone = cl.getMethod("clone", null);
        methodClone.setAccessible(true);
        return methodClone;
    }
}
