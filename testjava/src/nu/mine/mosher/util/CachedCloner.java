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

    public void clone(Object obj)
    {
        try
        {
            Class cl = obj.getClass();
            Method methodClose = (Method)cache.get(cl);
            if (methodClose == null)
            {
                methodClose = cl.getMethod("clone",null);
                methodClose.setAccessible(true);
                cache.put(cl,methodClose);
            }
            methodClose.invoke(obj,null);
        }
        catch (Throwable ignore)
        {
            ignore.printStackTrace();
        }
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

    private static Cloneable clone(Cloneable cloneableObject, Method methodClone)
        throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
    {
        return (Cloneable)methodClone.invoke(cloneableObject, null);
    }

    private static Method getCloneMethod(Class cl) throws SecurityException, NoSuchMethodException
    {
        Method methodClone;
        methodClone = cl.getMethod("clone", null);
        methodClone.setAccessible(true);
        return methodClone;
    }
}
