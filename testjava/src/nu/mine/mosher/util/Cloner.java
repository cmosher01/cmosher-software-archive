package com.surveysampling.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a static method that will clone any Cloneable Object.
 * This class needs to be in the java.lang package in order to be
 * able to access the protected clone method of Object.
 */
public final class Cloner
{
    private static final Map /*<Class,Method>*/ mClasses = new HashMap();

    private Cloner()
    {
        throw new UnsupportedOperationException();
    }

    public static Cloneable cloneObject(Cloneable cloneableObject) throws CloneNotSupportedException
    {
        try
        {
            Class cl = cloneableObject.getClass();
            Method methodClone = (Method)mClasses.get(cl);
            if (methodClone == null)
            {
                methodClone = cl.getMethod("clone",null);
                methodClone.setAccessible(true);
                mClasses.put(cl,methodClone);
            }
            return (Cloneable)methodClone.invoke(cloneableObject,null);
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
