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
}
