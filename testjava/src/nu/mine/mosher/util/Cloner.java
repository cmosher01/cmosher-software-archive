package com.surveysampling.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a static method that will clone any Cloneable Object.
 * This class reflection in order to be
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
                methodClone = getCloneMethod(cl);
                mClasses.put(cl,methodClone);
            }
            return clone(cloneableObject, methodClone);
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

    private static Cloneable clone(Cloneable cloneableObject, Method methodClone) throws CloneNotSupportedException
        throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        return (Cloneable)methodClone.invoke(cloneableObject,null);
    }

    public static Method getCloneMethod(Class cl) throws NoSuchMethodException, SecurityException
    {
        Method methodClone;
        methodClone = cl.getMethod("clone",null);
        methodClone.setAccessible(true);
        return methodClone;
    }
}
