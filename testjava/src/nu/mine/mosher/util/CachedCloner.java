package com.surveysampling.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class CachedCloner
{
    private final Map cache;

    public CachedCloner(Map cache)
    {
        if (cache == null)
        {
            throw new NullPointerException();
        }
        this.cache = cache;
    }

    public Cloneable cloneObject(Cloneable cloneableObject) throws CloneNotSupportedException
    {
        try
        {
            Class cl = cloneableObject.getClass();
            Method methodClone = (Method)cache.get(cl);
            if (methodClone == null)
            {
                methodClone = getCloneMethod(cl);
                cache.put(cl, methodClone);
            }
            return clone(cloneableObject, methodClone);
        }
        catch (Throwable cause)
        {
            CloneNotSupportedException ex = new CloneNotSupportedException();
            ex.initCause(cause);
            throw ex;
        }
    }

    private static Cloneable clone(Cloneable cloneableObject, Method methodClone)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        Cloneable clon = (Cloneable)methodClone.invoke(cloneableObject, null);
        return clon;
    }

    private static Method getCloneMethod(Class cl) throws SecurityException, NoSuchMethodException
    {
        Method methodClone;
        methodClone = cl.getMethod("clone", null);
        methodClone.setAccessible(true);
        return methodClone;
    }
}
