package com.surveysampling.util;

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
                methodClone = Cloner.getCloneMethod(cl);
                cache.put(cl, methodClone);
            }
            return Cloner.clone(cloneableObject, methodClone);
        }
        catch (CloneNotSupportedException e)
        {
            throw e;
        }
        catch (Throwable cause)
        {
            CloneNotSupportedException ex = new CloneNotSupportedException();
            ex.initCause(cause);
            throw ex;
        }
    }

}
